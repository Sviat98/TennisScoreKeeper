package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable

// Compose Multiplatform derives `preferredStatusBarStyle` from the composition's own
// LocalSystemTheme, which `LocalAppTheme.provides` already overrides, so there is nothing
// extra to push to UIKit here.
@Composable
actual fun updateSystemBars(isDark: Boolean) = Unit
