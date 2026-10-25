package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme

// NOTE: In Compose Multiplatform 1.11.1 these symbols live in `androidx.compose.ui`, NOT in
// `androidx.compose.ui.platform` as the install-theme-switcher skill states.
@OptIn(InternalComposeUiApi::class)
actual object LocalAppTheme {
    actual val current: Boolean
        @Composable get() = LocalSystemTheme.current == SystemTheme.Dark

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val new = when (value) {
            true -> SystemTheme.Dark
            false -> SystemTheme.Light
            null -> LocalSystemTheme.current
        }
        return LocalSystemTheme.provides(new)
    }
}
