# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Android debug build
./gradlew :androidApp:assembleDebug

# Android release build
./gradlew :androidApp:assembleRelease

# Desktop (JVM) run
./gradlew :composeApp:run

# Web (Wasm) dev server
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Run Android unit tests
./gradlew :androidApp:testDebugUnitTest

# Run Android instrumented tests
./gradlew :androidApp:connectedDebugAndroidTest

# Run a single test class
./gradlew :androidApp:testDebugUnitTest --tests "com.bashkevich.tennisscorekeeper.ExampleUnitTest"
```

Build mode is controlled via `BUILD_MODE` env var or Gradle property (defaults to `DEBUG`).

## Project Structure

**Kotlin Multiplatform** project targeting Android, Desktop (JVM), and Web (Kotlin/Wasm).

- `androidApp/` — Thin Android shell: `MainActivity` sets content to shared `App()` composable. Debug variant uses `.debug` applicationId suffix.
- `composeApp/` — Shared code module with platform-specific source sets:
  - `commonMain` — All shared business logic, UI, navigation, DI, networking
  - `androidMain` — Android-specific HTTP client (OkHttp), DataStore settings, media player
  - `desktopMain` — JVM/OkHttp client, Swing coroutines
  - `wasmJsMain` — JS HTTP client, observable settings

Base package: `com.bashkevich.tennisscorekeeper`

## Architecture

**MVI pattern** built on top of Compose + ViewModel:

- `BaseViewModel<UiState, UiEvent, UiAction>` — abstract base with `state: Flow<T>` and a `Channel<A>` for one-shot actions. All ViewModels extend this.
- Screens follow the convention: `screens/<feature>/<Feature>ViewModel.kt` + `<Feature>State.kt`

**Clean Architecture layers** (all in `commonMain`):

- `model/<domain>/domain/` — Domain models
- `model/<domain>/remote/` — DTOs and remote data sources (Ktor HTTP calls, WebSockets)
- `model/<domain>/repository/` — Repository interfaces + implementations
- `screens/` — ViewModels + UI state classes

**Dependency Injection**: Koin with modules in `di/`:
- `CoreModule` — Ktor HTTP client (with bearer auth + auto token refresh), `KeyValueStorage`, `AppViewModel`
- `AuthModule`, `TournamentModule`, `MatchModule`, `ParticipantModule`, `SetTemplateModule` — feature-specific repos and data sources
- `AppModule` — declares `expect val platformModule: Module` resolved per-platform

**Networking**: Ktor client with bearer token auth, ContentNegotiation (kotlinx.serialization JSON), WebSockets. Token refresh handled automatically. API host configured via `AppConfig`/`BuildConfig`.

**Navigation**: Type-safe Compose Navigation with `@Serializable` route objects in `navigation/Navigation.kt`. Each route has a `@SerialName` annotation. There's an `expect fun NavGraphBuilder.platformSpecificRoutes()` for platform-specific screens.

**Local storage**: Multiplatform Settings (backed by DataStore on Android/Desktop).

## Key Dependencies

- Compose Multiplatform 1.11.0 + Material 3 Adaptive
- Kotlin 2.3.21, AGP 9.0.0
- Koin 4.2.1 (BOM), Ktor 3.5.0
- Kotlinx Serialization, Kotlinx DateTime, Kotlinx Coroutines
- AndroidX Navigation Compose 2.9.2, Lifecycle 2.10.0
- compileSdk/targetSdk 36, minSdk 24

## Conventions

- All new shared code goes in `composeApp/src/commonMain`
- Use `expect`/`actual` for platform-specific implementations
- Route objects are `@Serializable` data objects/classes with explicit `@SerialName` to avoid cross-platform route parsing issues (see comments in Navigation.kt)
- ViewModels use `Flow<UiState>` for state and `Channel<UiAction>` for side effects
- DI is Koin — register new dependencies in the appropriate feature module in `di/`
