package com.bashkevich.tennisscorekeeper.components.environment

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String): ProvidedValue<*> {
        val newLocale = Locale(value)
        Locale.setDefault(newLocale)

        val currentConfig = LocalConfiguration.current
        val newConfig = Configuration(currentConfig).apply { setLocale(newLocale) }

        val resources = LocalContext.current.resources
        @Suppress("DEPRECATION")
        resources.updateConfiguration(newConfig, resources.displayMetrics)

        return LocalConfiguration.provides(newConfig)
    }
}
