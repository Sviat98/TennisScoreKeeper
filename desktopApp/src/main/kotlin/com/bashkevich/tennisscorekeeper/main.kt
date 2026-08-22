package com.bashkevich.tennisscorekeeper

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TennisScoreKeeper",
    ) {
        SideEffect {
            window.iconImage = loadAppIcon()
        }
        App()
    }
}

private fun loadAppIcon(): BufferedImage? = try {
    Thread.currentThread().contextClassLoader
        ?.getResourceAsStream("app-icon-512.png")
        ?.use(ImageIO::read)
} catch (_: IOException) {
    null
}
