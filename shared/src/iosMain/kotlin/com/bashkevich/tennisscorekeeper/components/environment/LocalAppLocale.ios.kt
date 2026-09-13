package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual object LocalAppLocale {
    // Compose Resources on iOS reads the resource locale from `AppleLanguages`, so the override
    // has to be written there — see the "Locale" section of compose-resource-environment.
    private const val LANGUAGE_KEY = "AppleLanguages"
    private val default: String = NSLocale.currentLocale.languageCode

    private val localeLocal = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = localeLocal.current

    @Composable
    actual infix fun provides(value: String): ProvidedValue<*> {
        val userDefaults = NSUserDefaults.standardUserDefaults
        if (value == default) {
            userDefaults.removeObjectForKey(LANGUAGE_KEY)
        } else {
            userDefaults.setObject(listOf(value), LANGUAGE_KEY)
        }
        return localeLocal.provides(value)
    }
}
