package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale

// Bypass the read-only `window.navigator.languages` (overridden by the index.html patch).
// Per https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html#locale
external object window {
    var __customLocale: String?
}

actual object LocalAppLocale {

    private val localeLocal = staticCompositionLocalOf { Locale.current }

    actual val current: String
        @Composable get() = localeLocal.current.toString()

    @Composable
    actual infix fun provides(value: String): ProvidedValue<*> {
        window.__customLocale = value.replace('_', '-')
        return localeLocal.provides(Locale.current)
    }
}
