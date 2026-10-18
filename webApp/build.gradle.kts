import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenExec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.browser)
            implementation(libs.androidx.navigation)
        }
    }
}

tasks.named<BinaryenExec>("compileProductionExecutableKotlinWasmJsOptimize") {
    // Size-focused -Oz output crashes macOS CEF 127 with Material 3 progress indicators.
    binaryenArguments.set(binaryenArguments.get().map { if (it == "-Oz") "-O3" else it })
}
