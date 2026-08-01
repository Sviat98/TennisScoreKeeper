package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Material 3 baseline color schemes. Start from defaults; override specific tokens
// (e.g. primary = Color(0xFF1B6C1B)) here if a brand palette is needed later.
private val LightColors = lightColorScheme()

private val DarkColors = darkColorScheme()

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    // Resolves the forced choice (LIGHT/DARK) or the system theme (SYSTEM) via LocalAppTheme.
    val darkTheme = LocalAppTheme.current
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
