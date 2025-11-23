import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}
val rawBuildMode: String = providers
    .environmentVariable("BUILD_MODE")
    .orElse(providers.gradleProperty("BUILD_MODE"))
    .orElse("debug")
    .get()

val normalizedBuildMode = when (rawBuildMode.lowercase()) {
    "release" -> "RELEASE"
    "debug" -> "DEBUG"
    else -> error("Unknown BUILD_MODE: $rawBuildMode (expected: debug or release)")
}

println("normalizedBuildMode = $normalizedBuildMode")

tasks.register("generateBuildConfig") {
    val outputDir = layout.buildDirectory.dir("generated/source/buildConfig/commonMain/kotlin")
    outputs.dir(outputDir)

    // Отключаем проверку актуальности таска
    outputs.upToDateWhen { false }

    doLast {
        val pkg = "com.bashkevich.tennisscorekeeper"
        val file = outputDir.get().file("BuildConfig.kt").asFile
        file.parentFile.mkdirs()
        file.writeText("""
            package $pkg

            object BuildConfig {
                val buildMode: BuildMode = BuildMode.$normalizedBuildMode
            }
        """.trimIndent())
    }
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.slf4j.android)
            implementation(libs.multiplatform.settings.datastore)
            implementation(libs.androidx.datastore.preferences)
        }
        val commonMain by getting{
            kotlin.srcDir(tasks.named("generateBuildConfig").map { it.outputs.files.singleFile })
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.compose.material3.adaptive)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.material.icons.core)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation)

            implementation(libs.kotlinx.datetime)

            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.websockets)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)

            implementation(libs.calf.file.picker)
            implementation(libs.compose.colorpicker)
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.logback.classic)
            implementation(libs.multiplatform.settings.datastore)

        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)

            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.make.observable)
        }
    }
}

tasks.named("compileKotlinMetadata") {
    dependsOn("generateBuildConfig")
}

android {
    namespace = "com.bashkevich.tennisscorekeeper"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.bashkevich.tennisscorekeeper"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies{
        coreLibraryDesugaring(libs.android.core.desugaring)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.bashkevich.tennisscorekeeper"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

