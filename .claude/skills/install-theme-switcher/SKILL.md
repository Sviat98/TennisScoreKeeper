---
name: install-theme-switcher
description: Installs a runtime app theme switcher (System / Light / Dark) into a Kotlin Multiplatform / Compose Multiplatform project (Android + iOS + Desktop + Web/Wasm). Uses the official "LocalAppTheme" expect/actual workaround from the Kotlin docs (compose-resource-environment) so the override also applies globally to isSystemInDarkTheme() (useful for third-party libraries). Persists the choice in DataStore, adds light/dark Material 3 color schemes, and surfaces the picker on a settings screen. Run this when the user wants in-app light/dark theme switching that survives restart on the project's targets.
---

# Install app theme (light/dark) switcher

This skill adds a **System / Light / Dark** theme switcher to a Kotlin
Multiplatform / Compose Multiplatform app. The choice is persisted in **DataStore**
and applied app-wide on **Android, iOS, Desktop and Web (Wasm)** — whichever of these
the project actually targets.

> **Placeholder.** Code below uses `com.example.app` as a placeholder for the target
> project's base package. Replace it everywhere — dotted in `package`/`import`
> statements (`com.example.app`) and slash-separated in file paths
> (`com/example/app`). If the shared/common module is not called `shared`, rename that
> too.

## Approach & why (global override)

The official Kotlin docs page
<https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html>
documents the `LocalAppTheme` expect/actual workaround. It overrides what
`isSystemInDarkTheme()` returns for the **whole wrapped subtree** — your code *and*
any third-party library that reads `isSystemInDarkTheme()` directly. This global
override is preferable to the simpler "pick `colorScheme` directly" approach: it
keeps every dark/light decision consistent with the user's choice, including code
you don't control.

How it works (per the docs):
- Android overrides `LocalConfiguration.uiMode`
  (`Resources.getConfiguration().uiMode and Configuration.UI_MODE_NIGHT_MASK`).
- **iOS, Desktop and Web** all override `LocalSystemTheme` directly with the **same**
  `actual` body.
- `LocalAppTheme.current` always resolves to the **effective** Boolean
  (the user's forced choice, or the system theme when "System" is selected).
- An `AppTheme` composable passes `lightColorScheme()` / `darkColorScheme()` to
  `MaterialTheme` based on `LocalAppTheme.current`.

> No `key(...)` is needed for theme (unlike locale). Providing a new
> `LocalSystemTheme` / `LocalConfiguration` via `CompositionLocalProvider` already
> recomposes every reader of `isSystemInDarkTheme()` / `LocalAppTheme.current`.

## Targets

This skill ships `actual` implementations for **Android, iOS, Desktop (JVM) and Web
(Wasm)**. Not every project targets all four — run **Step 0** to see which `actual`s
the project needs, and create only those.

## Prerequisites / conventions

**Ask the user which control form they want for the picker BEFORE implementing Step 9 — don't
assume.** Common options for the System / Light / Dark choice: a `SingleChoiceSegmentedButtonRow`
(compact; the default shown in Step 9), a `RadioButton` list (robust on every target, including
Wasm — `SegmentedButton` has known off-by-one rendering/hit-test quirks on Kotlin/Wasm), a
`Switch` (binary only — needs the choice cut to 2 options), or a dropdown. Confirm the form with
the user, then adapt the Step 9 code accordingly.

Assume the target project follows the standard KMP + Compose Multiplatform stack:

- **Persistence:** a DataStore-backed key-value store (often a wrapper named
  `KeyValueStorage`). Add the new key + save/observe functions there; if the project
  exposes DataStore directly instead, adapt accordingly.
- **MVI:** a `BaseViewModel<UiState, UiEvent, UiAction>` base with
  `abstract val state: Flow<T>`. Adapt the ViewModel code to the project's actual base.
- **DI:** Koin DSL. With the K2 compiler plugin: `single<Impl>().bind(Iface::class)`,
  `viewModel<VM>()` (`org.koin.plugin.module.dsl.*`). With classic Koin DSL, use the
  equivalent `single { ... }` / `viewModel { ... }` forms. Modules are registered in
  the root composable inside `KoinApplication { modules(...) }`.
- **Root app state:** a root ViewModel holding app-wide state (often `AppViewModel`
  with an `AppState`).
- **Root composable:** the app's content is wrapped by a `MaterialTheme { }` block
  (often in `App.kt`). This skill replaces it with an `AppTheme { }` wrapper so the
  chosen theme drives `MaterialTheme.colorScheme`.

> If `install-locale-switcher` is also installed, the two skills **share**
> `SettingsRepository`, the settings ViewModel, the root app ViewModel/state, and the
> `settingsModule`. Add the theme slice to those existing files — do not duplicate them.

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
| `androidTarget()` | `androidMain` | Android `actual` — uses `LocalConfiguration`. |
| `jvm("desktop")` | `desktopMain` | JVM `actual` — uses `LocalSystemTheme`. If the project uses `jvm()` instead, the set is `jvmMain`. |
| `iosX64()`, `iosArm64()`, `iosSimulatorArm64()` | `iosMain` (intermediate) | One `actual` in `iosMain` covers all three via the default hierarchy template. If the project disables source grouping, put the same `actual` in each of `iosX64Main` / `iosArm64Main` / `iosSimulatorArm64Main`. |
| `wasmJs { browser() }` / `wasmJs()` | `wasmJsMain` | Uses `LocalSystemTheme`. No `index.html` change needed for theme. |

> For theme, iOS / Desktop / Web share an **identical** `actual` body, so you create
> the same file (modulo the directory) in each of those source sets the project has.

Carry this list into **Step 5**: create the `expect` once in `commonMain`, and one
`actual` per source set identified above. Skip any platform the project doesn't
target.

## Step 1 — Add string resources

Append to **both** the default and any localized `strings.xml` under the project's
Compose Multiplatform resources (typically
`shared/src/commonMain/composeResources/values/strings.xml` and
`values-ru/strings.xml`):

`values/strings.xml` (English / default):
```xml
<!-- App theme switcher -->
<string name="app_theme">Theme</string>
<string name="theme_system">System default</string>
<string name="theme_light">Light</string>
<string name="theme_dark">Dark</string>
```

`values-ru/strings.xml` (Russian):
```xml
<!-- App theme switcher -->
<string name="app_theme">Тема</string>
<string name="theme_system">Системная</string>
<string name="theme_light">Светлая</string>
<string name="theme_dark">Тёмная</string>
```

## Step 2 — Domain model

Create `shared/src/commonMain/kotlin/com/example/app/model/settings/domain/AppThemeMode.kt`:

```kotlin
package com.example.app.model.settings.domain

/** App theme options offered in the switcher. */
enum class AppThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromName(name: String?): AppThemeMode =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}
```

## Step 3 — Persist in DataStore

Add the theme key + save/observe functions to the project's DataStore-backed
key-value store (e.g. `KeyValueStorage`). If the project has no such wrapper, add
these next to wherever it creates its `DataStore<Preferences>`:

```kotlin
import com.example.app.model.settings.domain.AppThemeMode

// ...
private val APP_THEME_KEY = stringPreferencesKey("appTheme")

suspend fun saveAppThemeMode(mode: AppThemeMode) {
    dataStore.edit { prefs ->
        if (mode == AppThemeMode.SYSTEM) prefs.remove(APP_THEME_KEY)
        else prefs[APP_THEME_KEY] = mode.name
    }
}

fun observeAppThemeMode(): Flow<AppThemeMode> =
    dataStore.data.map { prefs -> AppThemeMode.fromName(prefs[APP_THEME_KEY]) }
```

(`SYSTEM` is stored as absence of the key → "follow system".)

## Step 4 — Repository

If `SettingsRepository` does not yet exist, create it at
`shared/src/commonMain/kotlin/com/example/app/model/settings/repository/SettingsRepository.kt`
and add the theme slice. If the locale skill already created it, **add** these members
to the existing interface and impl.

```kotlin
package com.example.app.model.settings.repository

import com.example.app.core.local.KeyValueStorage
import com.example.app.model.settings.domain.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // locale members may also live here when install-locale-switcher is installed
    fun observeAppThemeMode(): Flow<AppThemeMode>
    suspend fun saveAppThemeMode(mode: AppThemeMode)
}

class SettingsRepositoryImpl(
    private val keyValueStorage: KeyValueStorage,
) : SettingsRepository {

    override fun observeAppThemeMode(): Flow<AppThemeMode> =
        keyValueStorage.observeAppThemeMode()

    override suspend fun saveAppThemeMode(mode: AppThemeMode) {
        keyValueStorage.saveAppThemeMode(mode)
    }
}
```

> If you do not use a `KeyValueStorage` wrapper, inject the `DataStore<Preferences>`
> (or the persistence layer the project uses) and inline the save/observe logic.

## Step 5 — `LocalAppTheme` expect + actuals

Create the `expect` in `commonMain`, then one `actual` **per target identified in
Step 0**.

### 5.1 commonMain

`shared/src/commonMain/kotlin/com/example/app/components/environment/LocalAppTheme.kt`:

```kotlin
package com.example.app.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Workaround for runtime theme override per
 * https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html
 *
 * `provides(value)` overrides the platform system theme so that
 * `isSystemInDarkTheme()` (and therefore [current]) returns the user's choice across
 * the whole wrapped subtree, including third-party code. `null` = follow the system.
 */
expect object LocalAppTheme {
    val current: Boolean @Composable get

    @Composable
    infix fun provides(value: Boolean?): ProvidedValue<*>
}
```

### 5.2 androidMain (only if the project has the Android target)

`shared/src/androidMain/kotlin/com/example/app/components/environment/LocalAppTheme.android.kt`:

```kotlin
package com.example.app.components.environment

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration

actual object LocalAppTheme {
    actual val current: Boolean
        @Composable get() =
            (LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val currentConfig = LocalConfiguration.current
        val new = if (value == null) {
            currentConfig
        } else {
            Configuration(currentConfig).apply {
                uiMode = when (value) {
                    true -> (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                    false -> (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
                }
            }
        }
        return LocalConfiguration.provides(new)
    }
}
```

### 5.3 iosMain, desktopMain, wasmJsMain (identical bodies)

Per the Kotlin docs, **iOS uses the same `LocalSystemTheme` override as Desktop and
Web**. Create this exact file in each of the source sets the project has (see Step 0):

- `shared/src/iosMain/kotlin/com/example/app/components/environment/LocalAppTheme.ios.kt`
- `shared/src/desktopMain/kotlin/com/example/app/components/environment/LocalAppTheme.desktop.kt`
- `shared/src/wasmJsMain/kotlin/com/example/app/components/environment/LocalAppTheme.wasmJs.kt`

```kotlin
package com.example.app.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.platform.LocalSystemTheme
import androidx.compose.ui.platform.SystemTheme

@OptIn(InternalComposeUiApi::class)
actual object LocalAppTheme {
    actual val current: Boolean
        @Composable get() = LocalSystemTheme.current == SystemTheme.Dark

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val new = when (value) {
            true -> SystemTheme.Dark
            false -> SystemTheme.Light
            null -> LocalSystemTheme.current
        }
        return LocalSystemTheme.provides(new)
    }
}
```

> `@OptIn(InternalComposeUiApi::class)` is required because `LocalSystemTheme` /
> `SystemTheme` are internal Compose UI APIs. If a future Compose release promotes them
> to public, drop the annotation.

## Step 6 — Light / dark color schemes + `AppTheme` composable

Create `shared/src/commonMain/kotlin/com/example/app/components/theme/AppTheme.kt`.
If the project already wraps its content in a custom theme composable, fold this in;
otherwise this becomes the new theme entry point. Start from Material 3 defaults and
tweak as desired:

```kotlin
package com.example.app.components.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.app.components.environment.LocalAppTheme

private val LightColors = lightColorScheme(
    // defaults are fine; override only what you want, e.g.:
    primary = Color(0xFF1B6C1B),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7ED957),
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = LocalAppTheme.current   // resolves forced choice OR system
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
```

## Step 7 — Thread theme into the root app state / ViewModel

Add an `appThemeMode` field to the root app state (shown here as `AppState`) and
collect it reactively. Replace the existing fields/deps with the project's real ones:

```kotlin
@Immutable
data class AppState(
    // ...your existing fields...
    val appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    // val appLanguage: AppLanguage = AppLanguage.SYSTEM,  // when locale skill also installed
) : UiState {
    companion object {
        fun initial() = AppState(/* ...existing defaults... */)
    }
}
```

Inject `SettingsRepository` into the root ViewModel (here `AppViewModel`) and add a
collector next to the existing ones:

```kotlin
class AppViewModel(
    // ...your existing dependencies...
    private val settingsRepository: SettingsRepository,   // <-- add
) : ViewModel() {
    // ...
    init {
        // ...existing collectors...
        viewModelScope.launch {
            settingsRepository.observeAppThemeMode().distinctUntilChanged().collect { mode ->
                _state.value = _state.value.copy(appThemeMode = mode)
            }
        }
    }
}
```

## Step 8 — Apply the override in the root composable

In the root composable (often `App.kt`), wrap the existing `MaterialTheme { ... }` /
app content block.

### Theme skill installed alone

```kotlin
val themeIsDarkOverride: Boolean? = when (appState.value.appThemeMode) {
    AppThemeMode.SYSTEM -> null
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
}

CompositionLocalProvider(LocalAppTheme provides themeIsDarkOverride) {
    AppTheme {
        // your existing NavHost { composable(...) { ... } } / app content
    }
}
```

Imports:
```kotlin
import androidx.compose.runtime.CompositionLocalProvider
import com.example.app.components.environment.LocalAppTheme
import com.example.app.components.theme.AppTheme
import com.example.app.model.settings.domain.AppThemeMode
```

### Both features installed (theme + locale)

Nest theme **outermost** (so the `isSystemInDarkTheme()` override reaches everything,
including `AppTheme`'s `LocalAppTheme.current` and any library), locale **inside**
(with its mandatory `key(...)` for string refresh):

```kotlin
val appLocaleTag = appState.value.appLanguage.tag
val themeIsDarkOverride: Boolean? = when (appState.value.appThemeMode) {
    AppThemeMode.SYSTEM -> null
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
}

CompositionLocalProvider(LocalAppTheme provides themeIsDarkOverride) {
    CompositionLocalProvider(LocalAppLocale provides appLocaleTag) {
        key(appLocaleTag) {
            AppTheme {
                // your existing NavHost { composable(...) { ... } } / app content
            }
        }
    }
}
```

## Step 9 — Picker UI on the settings screen

### 9.1 ViewModel slice

If the settings ViewModel does not exist, create it at
`shared/src/commonMain/kotlin/com/example/app/screens/settings/general/GeneralSettingsViewModel.kt`
(template mirrors the locale skill). Add the theme slice to its state/event:

```kotlin
@Immutable
data class GeneralSettingsState(
    val appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    // val appLanguage: AppLanguage = AppLanguage.SYSTEM,  // when locale skill also installed
) : UiState {
    companion object { fun initial() = GeneralSettingsState() }
}

@Immutable
sealed class GeneralSettingsUiEvent : UiEvent {
    data class ChangeThemeMode(val mode: AppThemeMode) : GeneralSettingsUiEvent()
}

class GeneralSettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<GeneralSettingsState, GeneralSettingsUiEvent, GeneralSettingsAction>() {

    override val state: StateFlow<GeneralSettingsState> =
        settingsRepository.observeAppThemeMode()
            .map { GeneralSettingsState(appThemeMode = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GeneralSettingsState.initial())

    fun onEvent(event: GeneralSettingsUiEvent) {
        when (event) {
            is GeneralSettingsUiEvent.ChangeThemeMode -> {
                viewModelScope.launch { settingsRepository.saveAppThemeMode(event.mode) }
            }
        }
    }
}
```

> When merging with the locale skill, build the state from `combine(
> observeAppThemeMode(), observeAppLanguage()) { mode, lang -> GeneralSettingsState(mode, lang) }`.

### 9.2 Screen slice

On the settings screen, render a `SingleChoiceSegmentedButtonRow`
(keep `themeModes` and `themeLabels` positionally aligned: SYSTEM, LIGHT, DARK):

```kotlin
val themeModes = AppThemeMode.entries
val themeLabels = listOf(
    stringResource(Res.string.theme_system),
    stringResource(Res.string.theme_light),
    stringResource(Res.string.theme_dark),
)

// inside the settings Column:
Text(
    text = stringResource(Res.string.app_theme),
    style = MaterialTheme.typography.titleMedium
)
SingleChoiceSegmentedButtonRow {
    themeModes.forEachIndexed { index, mode ->
        SegmentedButton(
            selected = state.appThemeMode == mode,
            onClick = { viewModel.onEvent(GeneralSettingsUiEvent.ChangeThemeMode(mode)) },
            shape = SegmentedButtonDefaults.itemShape(index, themeModes.size),
        ) { Text(themeLabels[index]) }
    }
}
```

## Step 10 — DI module + register it

If `settingsModule` does not exist (locale skill not installed), create
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

## Step 11 — Build & verify

Build/run each target the project has (adjust module/task names to the project):

```bash
./gradlew :androidApp:assembleDebug        # Android
./gradlew :shared:linkDebugFrameworkIosArm64  # iOS (or run the Xcode/iosApp scheme)
./gradlew :desktopApp:run                  # Desktop
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # Web
```

Verify on each target present:
- Settings screen → System / Light / Dark → entire app (Material 3 surfaces, bars,
  dialogs) switches palette immediately, without navigation state being lost.
- Restart → the chosen theme is restored (DataStore).
- With "System" selected → toggling the OS dark mode flips the app live (Android:
  system dark toggle; iOS: Settings → Display; Desktop/Web: OS theme).
- (Global override bonus) any composable reading `isSystemInDarkTheme()` directly follows the
  user's choice, not just the OS setting.

## Notes

- No `index.html` change is needed for theme (unlike locale) — `LocalSystemTheme` is
  overridden in-process on Web.
- If you later want Material You dynamic colors on Android 12+, swap the colorScheme
  inside `AppTheme` for `dynamicLightColorScheme(context)` / `dynamicDarkColorScheme(context)`
  on Android and keep `LightColors`/`DarkColors` as the fallback on other targets.
