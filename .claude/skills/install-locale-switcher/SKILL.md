---
name: install-locale-switcher
description: Installs a runtime language switcher into a Kotlin Multiplatform / Compose Multiplatform project (Android + iOS + Desktop + Web/Wasm). Uses the official "LocalAppLocale" expect/actual workaround from the Kotlin docs (compose-resource-environment), persists the choice in DataStore, and surfaces the picker on a settings screen. Run this when the user wants to add in-app language switching (System / English / Russian) that survives restart on the project's targets.
---

# Install locale (language) switcher

This skill adds a **runtime language switcher** to a Kotlin Multiplatform / Compose
Multiplatform app. The user can pick *System / English / Русский*; the choice is
persisted in **DataStore** and applied to the whole composition on **Android, iOS,
Desktop and Web (Wasm)** — whichever of these the project actually targets.

> **Placeholder.** Code below uses `com.example.app` as a placeholder for the target
> project's base package. Replace it everywhere — dotted in `package`/`import`
> statements (`com.example.app`) and slash-separated in file paths
> (`com/example/app`). If the shared/common module is not called `shared`, rename that
> too.

## Approach & why

The official Kotlin docs page
<https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html>
documents the only working way to switch locale at runtime in Compose
Multiplatform (verified against the `components-resources` 1.11.1 sources:
`ComposeEnvironment` / `LocalComposeEnvironment` / the `ResourceEnvironment`
constructor are all `internal`, so there is **no public override hook** — the only
lever is to change what `Locale.current` returns, which is exactly what this
workaround does).

Each platform overrides a different locale source (per the docs):
- Android: `context.resources.configuration.locale`
- iOS: `NSLocale.preferredLanguages` (persisted via `NSUserDefaults` "AppleLanguages")
- Desktop (JVM): `Locale.getDefault()`
- Web: `window.navigator.languages` (via an `index.html` patch)

Concretely: define an `expect object LocalAppLocale` + per-platform `actual`s that
rewrite the platform locale, and wrap the app content with
`CompositionLocalProvider(LocalAppLocale provides <tag>) { key(<tag>) { ... } }`.
The `key(...)` is mandatory — `stringResource(Res.string.*)` reads
`Locale.current` (a non-State read), so without `key()` the strings would not
refresh on change.

This skill assumes the app already ships Compose Multiplatform string resources
(typically `shared/src/commonMain/composeResources/values/strings.xml` for the
default/English and `values-ru/strings.xml` for Russian), and that
`stringResource(Res.string.*)` is already used. After installation, switching locale
makes those calls resolve from the matching `values-xx` directory.

## Targets

Ships `actual`s for **Android, iOS, Desktop (JVM) and Web (Wasm)**. Each platform has
its **own** `actual` body (unlike theme, they are not shared). Not every project
targets all four — run **Step 0** to see which `actual`s the project needs, and create
only those.

## Prerequisites / conventions

Assume the target project follows the standard KMP + Compose Multiplatform stack:

- **MVI:** a `BaseViewModel<UiState, UiEvent, UiAction>` base with
  `abstract val state: Flow<T>`, `protected val _action`, `sendAction`,
  `consumeAction`. Adapt to the project's actual base.
- **Persistence:** a DataStore-backed key-value store (often a wrapper named
  `KeyValueStorage`). Add the new key + save/observe functions there; if the project
  exposes DataStore directly instead, adapt accordingly.
- **DI:** Koin DSL. With the K2 compiler plugin: `single<Impl>().bind(Iface::class)`,
  `viewModel<VM>()` (`org.koin.plugin.module.dsl.*`). Modules are registered in the
  root composable inside `KoinApplication { modules(...) }`.
- **Resources:** `stringResource(Res.string.<name>)`; strings live under
  `shared/src/commonMain/composeResources/values/strings.xml` + `values-<tag>/strings.xml`.
- **iOS:** the iOS `actual` uses `platform.Foundation` (`NSLocale` / `NSUserDefaults`)
  and `kotlinx.cinterop` — both ship with Kotlin/Native, no extra dependency needed.
- **Settings screen:** a settings screen composable where the picker goes (often
  `GeneralSettingsScreen.kt`).

---

## Step 0 — Identify the project's targets

This skill uses `expect`/`actual`, so you must create an `actual` in **every**
compiled source set that sees `commonMain` — one per platform the project targets.
Create only those; an `actual` for a target the project doesn't have is dead code (and
a missing `actual` for a target it does have breaks compilation).

Inspect the shared module's Gradle script (usually `shared/build.gradle.kts`), find
the `kotlin { ... }` block, and map each target declaration to the source set where
the `actual` goes:

| Gradle target declaration | Source set for the `actual` | Notes |
| --- | --- | --- |
| `androidTarget()` | `androidMain` | Uses `LocalConfiguration` + `java.util.Locale`. |
| `iosX64()`, `iosArm64()`, `iosSimulatorArm64()` | `iosMain` (intermediate) | One `actual` in `iosMain` covers all three via the default hierarchy template. Uses `NSLocale` / `NSUserDefaults`. If source grouping is disabled, put the same `actual` in each of `iosX64Main` / `iosArm64Main` / `iosSimulatorArm64Main`. |
| `jvm("desktop")` | `desktopMain` | Uses `java.util.Locale` + `staticCompositionLocalOf`. If the project uses `jvm()`, the set is `jvmMain`. |
| `wasmJs { browser() }` / `wasmJs()` | `wasmJsMain` | Uses a `window.__customLocale` global + the `index.html` patch (Step 6). |

> Unlike theme, **every locale `actual` is different** — each platform rewrites its own
> locale source. Create exactly one per source set identified above, and skip the
> `index.html` patch (Step 6) if the project has no Web target.

Carry this list into **Step 5**: create the `expect` once in `commonMain`, and one
`actual` per source set identified above.

## Step 1 — Add string resources

Append to **both** the default and the localized `strings.xml` under the project's
Compose Multiplatform resources:

`values/strings.xml` (English / default):
```xml
<!-- Locale switcher -->
<string name="app_language">Language</string>
<string name="language_system">System</string>
<string name="language_english">English</string>
<string name="language_russian">Русский</string>
```

`values-ru/strings.xml` (Russian):
```xml
<!-- Locale switcher -->
<string name="app_language">Язык</string>
<string name="language_system">Системный</string>
<string name="language_english">English</string>
<string name="language_russian">Русский</string>
```

## Step 2 — Domain model

Create `shared/src/commonMain/kotlin/com/example/app/model/settings/domain/AppLanguage.kt`:

```kotlin
package com.example.app.model.settings.domain

/**
 * Languages offered in the switcher. [tag] is the BCP-47 language tag passed to the
 * platform locale (null = follow the system locale). The tag must match a
 * `values-<tag>` resource directory that exists under composeResources.
 */
enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    ENGLISH("en"),
    RUSSIAN("ru");

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: SYSTEM
    }
}
```

## Step 3 — Persist in DataStore

Add a key + save/observe functions to the project's DataStore-backed key-value store
(e.g. `KeyValueStorage`). If the project has no such wrapper, add these next to
wherever it creates its `DataStore<Preferences>`:

```kotlin
private val APP_LOCALE_KEY = stringPreferencesKey("appLocale")

suspend fun saveAppLocale(tag: String?) {
    dataStore.edit { prefs ->
        if (tag == null) prefs.remove(APP_LOCALE_KEY)
        else prefs[APP_LOCALE_KEY] = tag
    }
}

fun observeAppLocale(): Flow<String?> =
    dataStore.data.map { it[APP_LOCALE_KEY] }
```

(`null` stored as absence of the key → means "follow system".)

## Step 4 — Repository

If `SettingsRepository` does not yet exist, create it at
`shared/src/commonMain/kotlin/com/example/app/model/settings/repository/SettingsRepository.kt`.
If the theme skill already created it, **add** the locale members to the existing
interface and impl instead of creating a second one.

```kotlin
package com.example.app.model.settings.repository

import com.example.app.core.local.KeyValueStorage
import com.example.app.model.settings.domain.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    fun observeAppLanguage(): Flow<AppLanguage>
    suspend fun saveAppLanguage(language: AppLanguage)
}

class SettingsRepositoryImpl(
    private val keyValueStorage: KeyValueStorage,
) : SettingsRepository {

    override fun observeAppLanguage(): Flow<AppLanguage> =
        keyValueStorage.observeAppLocale().map { AppLanguage.fromTag(it) }

    override suspend fun saveAppLanguage(language: AppLanguage) {
        keyValueStorage.saveAppLocale(language.tag)
    }
}
```

> If you do not use a `KeyValueStorage` wrapper, inject the `DataStore<Preferences>`
> (or the persistence layer the project uses) and inline the save/observe logic.
>
> If `install-theme-switcher` is also being installed, **merge** both features into the
> same `SettingsRepository` / `SettingsRepositoryImpl`. Add
> `observeAppThemeMode()` / `saveAppThemeMode(themeMode)` here.

## Step 5 — `LocalAppLocale` expect + actuals

This is the workaround from the Kotlin docs. Create the `expect` in `commonMain`, then
one `actual` **per target identified in Step 0** — each platform's body is different.

### 5.1 commonMain

`shared/src/commonMain/kotlin/com/example/app/components/environment/LocalAppLocale.kt`:

```kotlin
package com.example.app.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Workaround for runtime locale override per
 * https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html
 *
 * `provides(tag)` rewrites the platform locale so that `stringResource(Res.string.*)`
 * resolves from the matching `values-<tag>` directory. `null` = follow the system locale.
 */
expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}
```

### 5.2 androidMain (only if the project has the Android target)

`shared/src/androidMain/kotlin/com/example/app/components/environment/LocalAppLocale.android.kt`:

```kotlin
package com.example.app.components.environment

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        if (default == null) {
            default = Locale.getDefault()
        }
        val new = when (value) {
            null -> default!!
            else -> Locale(value)
        }
        Locale.setDefault(new)
        configuration.setLocale(new)
        val resources = LocalContext.current.resources
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return LocalConfiguration.provides(configuration)
    }
}
```

### 5.3 iosMain (only if the project has an iOS target)

`shared/src/iosMain/kotlin/com/example/app/components/environment/LocalAppLocale.ios.kt` —
rewrites `NSLocale.preferredLanguages` via `NSUserDefaults` "AppleLanguages" (per the
Kotlin docs):

```kotlin
package com.example.app.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.cinterop.arrayListOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults

actual object LocalAppLocale {
    private const val LANG_KEY = "AppleLanguages"
    private val default = NSLocale.preferredLanguages.first() as String
    private val localeLocal = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = localeLocal.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(new), LANG_KEY)
        }
        return localeLocal.provides(new)
    }
}
```

> No `@OptIn(InternalComposeUiApi::class)` is needed here — `NSLocale` /
> `NSUserDefaults` / `staticCompositionLocalOf` are all public. (The iOS theme `actual`
> does need it, because of `LocalSystemTheme`.)
>
> `arrayListOf(...)` comes from `kotlinx.cinterop` and produces the `NSMutableArray`
> that `NSUserDefaults.setObject` expects for "AppleLanguages".

### 5.4 desktopMain / jvmMain (only if the project has a JVM/desktop target)

`shared/src/desktopMain/kotlin/com/example/app/components/environment/LocalAppLocale.desktop.kt`
(or `jvmMain` if the project uses `jvm()`):

```kotlin
package com.example.app.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null
    private val localeLocal = staticCompositionLocalOf { Locale.getDefault().toString() }

    actual val current: String
        @Composable get() = localeLocal.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        if (default == null) {
            default = Locale.getDefault()
        }
        val new = when (value) {
            null -> default!!
            else -> Locale(value)
        }
        Locale.setDefault(new)
        return localeLocal.provides(new.toString())
    }
}
```

### 5.5 wasmJsMain (only if the project has a Web/Wasm target)

`shared/src/wasmJsMain/kotlin/com/example/app/components/environment/LocalAppLocale.wasmJs.kt`:

```kotlin
package com.example.app.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import kotlinx.browser.window

actual object LocalAppLocale {
    private val localeLocal = staticCompositionLocalOf { Locale.current }

    actual val current: String
        @Composable get() = localeLocal.current.toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        // The index.html patch (Step 6) makes navigator.languages return this tag.
        window.asDynamic().__customLocale = value?.replace('_', '-')
        return localeLocal.provides(Locale.current)
    }
}
```

> `kotlinx.browser.window` is available on the WasmJs target. If for any reason the
> dependency is not on the wasmJs classpath, fall back to a top-level external:
> `private external object window { var __customLocale: String? }` (the official docs
> snippet) — both read/write the same global the `index.html` patch uses.

## Step 6 — `index.html` patch (Web/Wasm only)

> **Skip this step if the project has no Web (`wasmJs`) target** (see Step 0).

The locale workaround on Web needs `navigator.languages` to return the chosen tag.
Edit the web app's `index.html` (typically `webApp/src/wasmJsMain/resources/index.html`)
and insert this script **before** the `<script ... src="<webApp>.js">` line (it must
run before the app boots):

```html
<script>
    var currentLanguagesImplementation = Object.getOwnPropertyDescriptor(Navigator.prototype, "languages");
    var newLanguagesImplementation = Object.assign({}, currentLanguagesImplementation, {
        get: function () {
            if (window.__customLocale) {
                return [window.__customLocale];
            } else {
                return currentLanguagesImplementation.get.apply(this);
            }
        }
    });
    Object.defineProperty(Navigator.prototype, "languages", newLanguagesImplementation);
</script>
```

The resulting `<head>` looks like:

```html
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>App</title>
    <link type="text/css" rel="stylesheet" href="styles.css">
    <script>
        /* ...navigator.languages override from above... */
    </script>
    <script type="application/javascript" src="<webApp>.js"></script>
</head>
```

## Step 7 — Thread locale into the root app state / ViewModel

Add the field to the root app state (shown here as `AppState`) and observe it
reactively. Replace the existing fields/deps with the project's real ones:

```kotlin
@Immutable
data class AppState(
    // ...your existing fields...
    val appLanguage: AppLanguage = AppLanguage.SYSTEM,
) : UiState {
    companion object {
        fun initial() = AppState(/* ...existing defaults... */)
    }
}
```

```kotlin
class AppViewModel(
    // ...your existing dependencies...
    private val settingsRepository: SettingsRepository,   // <-- add
) : ViewModel() {

    private val _state = MutableStateFlow(AppState.initial())
    val state: StateFlow<AppState> get() = _state.asStateFlow()

    init {
        // ...existing collectors...

        viewModelScope.launch {
            settingsRepository.observeAppLanguage().distinctUntilChanged().collect { language ->
                _state.value = _state.value.copy(appLanguage = language)
            }
        }
    }
}
```

> Do **not** read the locale imperatively in `init` and stash it — keep it a `Flow`
> collected into state.

## Step 8 — Apply the override in the root composable

In the root composable (often `App.kt`), wrap the existing `MaterialTheme { ... }` /
app content block. Replace:

```kotlin
MaterialTheme {
    // your existing NavHost { composable(...) { ... } } / app content
}
```

with:

```kotlin
val appLocaleTag = appState.value.appLanguage.tag   // null = system

CompositionLocalProvider(LocalAppLocale provides appLocaleTag) {
    key(appLocaleTag) {
        MaterialTheme {
            // your existing NavHost { composable(...) { ... } } / app content
        }
    }
}
```

Add the imports:
```kotlin
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import com.example.app.components.environment.LocalAppLocale
```

> `key(appLocaleTag)` intentionally recreates the subtree on locale change so that
> every `stringResource(...)` re-resolves. **Known tradeoff of the workaround:** the
> NavHost is recreated, so the current navigation back-stack resets to
> `startDestination` on a language change. Acceptable for a settings-driven change.
>
> **If `install-theme-switcher` is also installed:** nest theme outer, locale inner.
> See the "Both features installed" section in that skill.

## Step 9 — Picker UI on the settings screen

### 9.1 ViewModel

If it does not exist, create the settings ViewModel at
`shared/src/commonMain/kotlin/com/example/app/screens/settings/general/GeneralSettingsViewModel.kt`:

```kotlin
package com.example.app.screens.settings.general

import androidx.lifecycle.viewModelScope
import com.example.app.model.settings.domain.AppLanguage
import com.example.app.model.settings.repository.SettingsRepository
import com.example.app.mvi.BaseViewModel
import com.example.app.mvi.UiAction
import com.example.app.mvi.UiEvent
import com.example.app.mvi.UiState
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
sealed class GeneralSettingsUiEvent : UiEvent {
    data class ChangeLanguage(val language: AppLanguage) : GeneralSettingsUiEvent()
}

@Immutable
data class GeneralSettingsState(
    val appLanguage: AppLanguage = AppLanguage.SYSTEM,
) : UiState {
    companion object {
        fun initial() = GeneralSettingsState()
    }
}

@Immutable
sealed class GeneralSettingsAction : UiAction

class GeneralSettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<GeneralSettingsState, GeneralSettingsUiEvent, GeneralSettingsAction>() {

    override val state: StateFlow<GeneralSettingsState> =
        settingsRepository.observeAppLanguage()
            .map { GeneralSettingsState(appLanguage = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GeneralSettingsState.initial())

    fun onEvent(event: GeneralSettingsUiEvent) {
        when (event) {
            is GeneralSettingsUiEvent.ChangeLanguage -> {
                viewModelScope.launch { settingsRepository.saveAppLanguage(event.language) }
            }
        }
    }
}
```

> If the theme skill is also installed, **add** `appThemeMode` to `GeneralSettingsState`
> + a `ChangeThemeMode` event here; do not create a second ViewModel.

### 9.2 Screen

Build a settings screen that injects the ViewModel and renders a
`SingleChoiceSegmentedButtonRow`. Skeleton (adapt the existing `Scaffold` +
`TopAppBar` to the project's real structure):

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: GeneralSettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavHostController.current

    val languages = AppLanguage.entries
    val labels = listOf(
        stringResource(Res.string.language_system),
        stringResource(Res.string.language_english),
        stringResource(Res.string.language_russian),
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.general_settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.app_language),
                style = MaterialTheme.typography.titleMedium
            )
            SingleChoiceSegmentedButtonRow {
                languages.forEachIndexed { index, language ->
                    SegmentedButton(
                        selected = state.appLanguage == language,
                        onClick = { viewModel.onEvent(GeneralSettingsUiEvent.ChangeLanguage(language)) },
                        shape = SegmentedButtonDefaults.itemShape(index, languages.size),
                    ) { Text(labels[index]) }
                }
            }
        }
    }
}
```

> `labels` is positional — keep it in the same order as `AppLanguage.entries`
> (SYSTEM, ENGLISH, RUSSIAN). Use the project's own icon entry if it has an icon set
> instead of `Icons.AutoMirrored.Filled.ArrowBack`.

## Step 10 — Wire the ViewModel in the nav graph

In the settings navigation graph (often `SettingsNavigationGraph.kt`), change the
settings destination from a plain `GeneralSettingsScreen()` to inject the VM:

```kotlin
composable<GeneralSettingsRoute> {
    val vm = koinViewModel<GeneralSettingsViewModel>()
    GeneralSettingsScreen(viewModel = vm)
}
```

## Step 11 — DI module + register it

If `settingsModule` does not exist (theme skill not installed), create
`shared/src/commonMain/kotlin/com/example/app/di/SettingsModule.kt`:

```kotlin
package com.example.app.di

import com.example.app.model.settings.repository.SettingsRepository
import com.example.app.model.settings.repository.SettingsRepositoryImpl
import com.example.app.screens.settings.general.GeneralSettingsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val settingsModule = module {
    single<SettingsRepositoryImpl>().bind(SettingsRepository::class)
    viewModel<GeneralSettingsViewModel>()
}
```

Register it in the root composable inside `KoinApplication { modules(...) }`:

```kotlin
modules(
    // ...the project's existing modules...
    settingsModule,   // <-- add
)
```

## Step 12 — Build & verify

Build/run each target the project has (adjust module/task names to the project):

```bash
./gradlew :androidApp:assembleDebug            # Android
./gradlew :shared:linkDebugFrameworkIosArm64   # iOS (or run the Xcode/iosApp scheme)
./gradlew :desktopApp:run                      # Desktop
./gradlew :webApp:wasmJsBrowserDevelopmentRun  # Web
```

Verify on each target present:
- Open the settings screen → tap English / Русский / System → all `stringResource`
  labels update immediately across the app.
- Restart the app → the chosen language is restored (DataStore).
- iOS: the choice is also written to `NSUserDefaults` "AppleLanguages", so a cold start
  picks it up even before the composition override is applied.
- On Web, confirm `navigator.languages` override is active (DevTools console:
  `navigator.languages`) and that `values-ru` strings load when Russian is picked.

## Adding more languages later

1. Add `values-<tag>/strings.xml` under `shared/src/commonMain/composeResources/`.
2. Add an `AppLanguage` entry with that `tag`.
3. Add a matching label string + a `SegmentedButton` entry (keep `labels` aligned with `entries`).
