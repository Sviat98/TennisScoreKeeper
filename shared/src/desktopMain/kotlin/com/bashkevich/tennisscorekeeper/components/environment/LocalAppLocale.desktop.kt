package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

actual object LocalAppLocale {

    private val localeLocal = staticCompositionLocalOf { Locale.getDefault().toString() }

    actual val current: String
        @Composable get() = localeLocal.current

    @Composable
    actual infix fun provides(value: String): ProvidedValue<*> {
        val newLocale = Locale(value)
        Locale.setDefault(newLocale)
        return localeLocal.provides(newLocale.toString())
    }
}
