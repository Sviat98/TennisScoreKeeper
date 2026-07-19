# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Android debug build
./gradlew :androidApp:assembleDebug

# Android release build
./gradlew :androidApp:assembleRelease

# Desktop (JVM) run
./gradlew :desktopApp:run

# Web (Wasm) dev server
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Run Android unit tests
./gradlew :androidApp:testDebugUnitTest

# Run Android instrumented tests
./gradlew :androidApp:connectedDebugAndroidTest

# Run a single test class
./gradlew :androidApp:testDebugUnitTest --tests "com.bashkevich.tennisscorekeeper.ExampleUnitTest"
```

Build mode is controlled via `BUILD_MODE` env var or Gradle property (defaults to `DEBUG`). DEBUG uses `tennisscorekeeper.onrender.com` API host, RELEASE uses `tennisscorekeeper.tech`.

## Project Structure

**Kotlin Multiplatform** project targeting Android, Desktop (JVM), and Web (Kotlin/Wasm).

- `androidApp/` — Thin Android shell: `MainActivity` sets content to shared `App()` composable. Debug variant uses `.debug` applicationId suffix.
- `desktopApp/` — Thin Desktop (JVM) shell: `main.kt` entry point (`compose.desktop` application). Depends on `shared`.
- `webApp/` — Thin Web (Wasm) shell: `main.kt` entry point (ComposeViewport), `index.html`/`styles.css`, `webpack.config.d` (COOP/COEP dev headers). Depends on `shared`.
- `shared/` — Shared code module with platform-specific source sets:
  - `commonMain` — All shared business logic, UI, navigation, DI, networking
  - `androidMain` — Android-specific HTTP client (OkHttp), DataStore settings, media player
  - `desktopMain` — JVM/OkHttp client, Swing coroutines
  - `wasmJsMain` — JS HTTP client, observable settings, `ScoreboardRoute` + the wasmJs-only Scoreboard feature (live match scoreboard via WebSocket)

Base package: `com.bashkevich.tennisscorekeeper`

## Architecture

**MVI pattern** built on top of Compose + ViewModel:

- `BaseViewModel<UiState, UiEvent, UiAction>` — abstract base with `state: Flow<T>` and `Channel<A>` for one-shot actions. All ViewModels extend this.
- Screens follow the convention: `screens/<feature>/<Feature>ViewModel.kt` + `<Feature>State.kt`
- ViewModels use `onEvent()` to handle user events, `reduceState()` for state updates, `sendAction()` for one-shot side effects

**Clean Architecture layers** (all in `commonMain`):

- `model/<domain>/domain/` — Domain models
- `model/<domain>/remote/` — DTOs and remote data sources (Ktor HTTP calls, WebSockets)
- `model/<domain>/local/` — Room DAOs and local data sources
- `model/<domain>/repository/` — Repository interfaces + implementations (combine remote + local)
- `screens/` — ViewModels + UI state classes

**Error handling**: `LoadResult<S, E>` sealed class (`Success` / `Error`) used across repositories with extensions like `mapSuccess`, `doOnSuccess`, `doOnError`, `runOperationCatching`.

**Dependency Injection**: Koin with modules in `di/`:
- `CoreModule` — Ktor HTTP client (with bearer auth + auto token refresh), `KeyValueStorage`, Room database builder, `AppViewModel`
- `AuthModule`, `TournamentModule`, `MatchModule`, `ParticipantModule`, `SetTemplateModule`, `ThemeModule` — feature-specific repos, data sources, and ViewModels
- `AppModule` — declares `expect val platformModule: Module` resolved per-platform

**Networking**: Ktor client with bearer token auth, ContentNegotiation (kotlinx.serialization JSON), WebSockets for real-time match updates. Token refresh handled automatically on 401. Custom origin header for CORS.

**Navigation**: Type-safe Compose Navigation with `@Serializable` route objects in `navigation/Navigation.kt`. Each route has a `@SerialName` annotation — use simple names to avoid cross-platform route parsing issues. There's an `expect fun NavGraphBuilder.platformSpecificRoutes()` for platform-specific screens (WasmJS has `ScoreboardRoute`).

**Local storage**:
- Room 3.0 database (`AppDatabase`) with 5 DAOs (Tournament, Match, Participant, SetTemplate, Theme). Platform-specific builders via `expect fun getDatabaseBuilder()`.
- Multiplatform Settings (backed by DataStore on Android/Desktop) wrapped as `KeyValueStorage`.

**Platform-specific expect/actual**:
- `PlatformConfiguration` — holds Android Context on Android, empty elsewhere
- `httpClient()` — OkHttp engine on Android/Desktop, JS engine on WasmJS
- `getDatabaseBuilder()` — SQLite on Android/Desktop, Web SQL on WasmJS
- `platformSpecificRoutes()` — WasmJS-only scoreboard route

## Key Dependencies

- Compose Multiplatform 1.11.1 + Material 3 Adaptive 1.2.0
- Kotlin 2.4.0, AGP 9.2.1
- Koin 4.2.1 (BOM), Ktor 3.5.0
- Room 3.0.0-alpha06, Kotlinx Serialization 1.11.0, Kotlinx DateTime 0.8.0, Kotlinx Coroutines 1.11.0
- AndroidX Navigation Compose 2.9.2, Lifecycle 2.11.0-beta01
- compileSdk/targetSdk 37, minSdk 24

## Conventions

- All new shared code goes in `shared/src/commonMain`
- Use `expect`/`actual` for platform-specific implementations
- Route objects are `@Serializable` data objects/classes with explicit `@SerialName` to avoid cross-platform route parsing issues (see comments in Navigation.kt)
- ViewModels use `Flow<UiState>` for state and `Channel<UiAction>` for side effects
- DI is Koin — register new dependencies in the appropriate feature module in `di/`
- Use `LoadResult<S, E>` for repository return types with proper error handling
- Room schema files go in `shared/schemas/`
