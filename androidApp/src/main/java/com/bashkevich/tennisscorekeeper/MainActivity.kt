package com.bashkevich.tennisscorekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chaintech.videoplayer.util.PlaybackPreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Режим берётся из product flavor (dev/prod) и должен быть выставлен до создания
        // Koin-графа в App(), иначе HttpClient уедет на дефолтный хост из BuildKonfig.
        AppConfig.setBuildMode(BuildMode.valueOf(BuildConfig.BUILD_MODE))
        AppConfig.logBuildMode()
        PlaybackPreference.initialize(this)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}