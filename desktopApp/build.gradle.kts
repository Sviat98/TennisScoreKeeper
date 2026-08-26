import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.compose.ui.tooling.preview)
}

// Прокидывает выбор бэкенда в запускаемую JVM: ./gradlew :desktopApp:run -PBUILD_MODE=RELEASE
// (читается в shared/src/desktopMain/.../BuildMode.desktop.kt).
val desktopBuildMode: String = providers.gradleProperty("BUILD_MODE")
    .orElse(providers.environmentVariable("BUILD_MODE"))
    .getOrElse("DEBUG")

compose.desktop {
    application {
        mainClass = "com.bashkevich.tennisscorekeeper.MainKt"
        jvmArgs += "-DBUILD_MODE=$desktopBuildMode"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.bashkevich.tennisscorekeeper"
            packageVersion = "1.0.0"
        }
    }
}
