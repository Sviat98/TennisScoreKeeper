package com.bashkevich.tennisscorekeeper.components.environment

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat

@SuppressLint("ComposableNaming")
@Composable
actual fun updateSystemBars(isDark: Boolean) {
    val window = LocalActivity.current?.window ?: return
    // SideEffect runs on every recomposition where isDark changes, so the bar icon
    // appearance is re-applied after each theme change (user override or system).
    SideEffect {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        // Light appearance => dark icons (for light theme); dark appearance => light icons.
        controller.isAppearanceLightStatusBars = !isDark
        controller.isAppearanceLightNavigationBars = !isDark
    }
}
