package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable

/**
 * Applies the resolved dark/light theme to the platform system bars so their icon
 * appearance (status bar + navigation bar) follows the app theme, not only the system theme.
 *
 * Call this from the top-level theme composable with the already-resolved `isDark` value
 * (e.g. [LocalAppTheme.current], which accounts for both the user's override and the system).
 *
 * Platform behaviour:
 * - Android: toggles `WindowInsetsControllerCompat` light/dark appearance flags reactively.
 * - Desktop / WasmJS: no-op (no comparable system-bar styling).
 * - iOS (future): hook `preferredStatusBarStyle` here.
 */
@Composable
expect fun updateSystemBars(isDark: Boolean)
