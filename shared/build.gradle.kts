import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.build.konfig)
    alias(libs.plugins.androidx.room3)
    alias(libs.plugins.koin.compiler)
}

kotlin {

    android{
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.targetSdk.get().toInt()
        namespace = "com.bashkevich.tennisscorekeeper.shared"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.slf4j.android)
            implementation(libs.androidx.datastore)

            implementation(libs.media.player)
            implementation(libs.sdp.ssp)

            implementation(libs.androidx.sqlite.bundled)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation)

            implementation(libs.kotlinx.datetime)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.websockets)

            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.datastore.core.okio)

            implementation(libs.calf.file.picker)
            implementation(libs.coil3.compose)
            implementation(libs.compose.colorpicker)

            implementation(libs.androidx.room3.runtime)

            implementation(libs.kmptoolkit.cmp.share)
            implementation(libs.kmptoolkit.cmp.share.compose)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.logback.classic)

            implementation(libs.androidx.sqlite.bundled)

        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)

            implementation(libs.androidx.sqlite.web)
            implementation(
                npm("sqlite-wasm-worker", layout.projectDirectory.dir("sqlite-wasm-worker").asFile)
            )
            implementation(libs.kotlinx.browser)
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspDesktop", libs.androidx.room3.compiler)
    add("kspWasmJs", libs.androidx.room3.compiler)
}

val buildMode  = providers.environmentVariable("BUILD_MODE")
    .orElse(providers.gradleProperty("BUILD_MODE"))
    .orElse("DEBUG")
    .get()

require(buildMode == "DEBUG" || buildMode == "RELEASE") { "Unknown BUILD_MODE: $buildMode (expected DEBUG or RELEASE)" }

// Обязательные URL без дефолтов: env -> gradle property, иначе ошибка сборки.
// Хвостовой слэш обрезается, чтобы endpoint-пути ("/tournaments" и т.п.) не склеивались в двойной слэш.
fun requiredUrl(name: String): String {
    val value = providers.environmentVariable(name).filter { it.isNotBlank() }
        .orElse(providers.gradleProperty(name).filter { it.isNotBlank() })
        .orElse("")
        .get()
        .trim()
        .trimEnd('/')
    if (value.isBlank()) {
        throw GradleException(
            "$name is not set. Pass it as an environment variable or Gradle property, " +
                "e.g. -P$name=https://your-host (in CI: BACKEND_URL/FRONTEND_URL build-args)."
        )
    }
    return value
}

val backendUrl = requiredUrl("BACKEND_URL")
val frontendUrl = requiredUrl("FRONTEND_URL")

buildkonfig {
    packageName = "com.bashkevich.tennisscorekeeper"
    objectName = "BuildConfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "buildMode",buildMode)
        buildConfigField(FieldSpec.Type.STRING, "backendUrl", backendUrl)
        buildConfigField(FieldSpec.Type.STRING, "frontendUrl", frontendUrl)
    }
}

println("buildMode = $buildMode")
println("backendUrl = $backendUrl")
println("frontendUrl = $frontendUrl")

gradle.taskGraph.whenReady {
    val lastTask = allTasks.lastOrNull()
    lastTask?.doLast {
        if (this.state.failure != null) return@doLast
        println("✅ TASK SUCCESSFUL. Some messages suppressed by logging.level=warn")
    }
}

