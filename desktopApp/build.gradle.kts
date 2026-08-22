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

compose.desktop {
    application {
        mainClass = "com.bashkevich.tennisscorekeeper.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.bashkevich.tennisscorekeeper"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.rootProject.file("artwork/app.icns"))
            }
            windows {
                iconFile.set(project.rootProject.file("artwork/app.ico"))
            }
            linux {
                iconFile.set(project.rootProject.file("artwork/png/app-icon-512.png"))
            }
        }
    }
}
