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
# Opens at http://127.0.0.1:8080 — use `127.0.0.1`, NOT `localhost`: the backend CORS allowlist
# (plugins/Cors.kt) contains `127.0.0.1:8080`/`127.0.0.1:8081` and does NOT include `localhost`,
# so a page opened via localhost gets 403 on every WebSocket upgrade.
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Run Android unit tests
./gradlew :androidApp:testDebugUnitTest

# Run Android instrumented tests
./gradlew :androidApp:connectedDebugAndroidTest

# Run a single test class
./gradlew :androidApp:testDebugUnitTest --tests "com.bashkevich.tennisscorekeeper.ExampleUnitTest"
```

Build mode is controlled via `BUILD_MODE` env var or Gradle property (defaults to `DEBUG`). DEBUG uses `tennisscorekeeper.onrender.com` API host, RELEASE uses `tennisscorekeeper.tech`.

## Running in browser (wasmJs)

```bash
# Dev server with RELEASE API hosts (env var must be in the same command — shell state does not persist between calls)
BUILD_MODE=RELEASE ./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

- Opens at `http://127.0.0.1:8080` (use `127.0.0.1`, NOT `localhost` — backend CORS rejects the `localhost` origin on WebSocket upgrades with 403; see `plugins/Cors.kt` in the backend).
- First build takes ~2–3 minutes; the wasm bundle is ~43 MiB.
- BUILD_MODE only selects API hosts (via BuildKonfig `BuildConfig.buildMode`), it does not change binary optimization.
- In ZCode's in-app browser the page is NOT cross-origin isolated (`crossOriginIsolated === false`) even though the dev server sends COOP/COEP headers, so sqlite OPFS fails with `sqlite3.oo1.OpfsDb is not a constructor` and webpack shows a red error overlay. The app still renders behind the overlay (dismiss via the top-right ×). In a real Chrome everything works, OPFS included. On Windows open the user's Chrome from Git Bash with `cmd //c start chrome "http://127.0.0.1:8080"` — the in-app browser automation backend cannot control an external Chrome.

### WebSocket (local testing)

- WS endpoint: `wss://<backend>/matches/{matchId}`. The route validates the match id — a non-existing id gets HTTP 404 on the upgrade; the client treats this as a connection error and retries with exponential backoff (5→10→20→40→60 s cap). Test only with an existing match id.
- CORS of the WS upgrade by `Origin`, per the backend's `plugins/Cors.kt` (requires an up-to-date backend deployment): `http://127.0.0.1:8080` / `:8081` → allowed; `https://tennisscorekeeper.onrender.com` → allowed; `https://tennisscorekeeper.tech` → allowed; `http://localhost:8080` → **403** (not in the allowlist). Older deployments had the opposite list (localhost allowed, 127.0.0.1 blocked) — if you see 403, check which backend version is deployed.
- Heartbeat protocol: the client (`MatchRemoteDataSource.connectToMatchUpdates`) sends `{"type":"heartbeat"}` after 15 s of server silence (const `HEARTBEAT_INTERVAL_MS`) and expects ANY message within 10 s; otherwise it closes the session and reconnects. The server answers by re-sending the current MatchDto snapshot (`isHeartbeatRequest` in the backend's `MatchRoutes.kt`) — so idle connections stay alive and the scoreboard gets a state refresh.
- `ConnectionState.Loading` (full-screen spinner on both MatchDetails and Scoreboard screens) is set only for the very first connection attempt; subsequent reconnect attempts keep `Disconnected` ("connection with scoreboard lost" message under the scoreboard strip on the Scoreboard screen).
- `connectToMatchUpdatesLegacy` is the pre-heartbeat implementation kept as a fallback; the active one is `connectToMatchUpdates`.
- A local mock WS server for e2e testing lives at `.zcode/mock-ws-server.cjs` (Node + `ws` from the kotlin-npm cache; WS on 8090, control HTTP on 8091: `/mode?s=silent|reply|kill`, `/log`, `/reset`). Pointing the client at it requires temporarily changing the URL in `MatchRemoteDataSource.connectToMatchUpdates`.

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
- **NEVER use `init {}` blocks in ViewModels.** Build state reactively from repository Flows (`combine` / `flatMapLatest` / `map { ... }.stateIn(...)`) on the `state` property. One-shot startup side effects go in the same flow's `.onStart { }` operator — not in `init {}`. This is a hard, always-applied rule.
- DI is Koin — register new dependencies in the appropriate feature module in `di/`
- Use `LoadResult<S, E>` for repository return types with proper error handling
- Room schema files go in `shared/schemas/`. After changing any Room entity, regenerate the schema by running `./gradlew :shared:kspKotlinDesktop` and commit the updated JSON (DB version bumps and migrations are handled separately by the repo owner)
