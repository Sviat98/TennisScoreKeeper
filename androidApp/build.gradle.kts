plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.bashkevich.tennisscorekeeper"
    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt())
    }

    defaultConfig {
        applicationId = "com.bashkevich.tennisscorekeeper"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions += "backend"
    productFlavors {
        // dev  -> tennisscorekeeperbackend.onrender.com / https://tennisscorekeeper.onrender.com
        // prod -> api.tennisscorekeeper.tech           / https://tennisscorekeeper.tech
        // Комбинируется с buildType: prodDebug = отлаживаемая сборка против прод-бэкенда.
        create("dev") {
            dimension = "backend"
            buildConfigField("String", "BUILD_MODE", "\"DEBUG\"")
        }
        create("prod") {
            dimension = "backend"
            buildConfigField("String", "BUILD_MODE", "\"RELEASE\"")
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            // Дополнительные настройки для debug
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.media.player)
    implementation(libs.sdp.ssp)
    testImplementation(libs.junit)
    coreLibraryDesugaring(libs.android.core.desugaring)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}