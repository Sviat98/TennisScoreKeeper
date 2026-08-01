package com.bashkevich.tennisscorekeeper.components.environment

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration

actual object LocalAppTheme {
    actual val current: Boolean
        @Composable get() =
            (LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val currentConfig = LocalConfiguration.current
        val new = if (value == null) {
            currentConfig
        } else {
            Configuration(currentConfig).apply {
                uiMode = when (value) {
                    true -> (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                    false -> (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
                }
            }
        }
        return LocalConfiguration.provides(new)
    }
}
