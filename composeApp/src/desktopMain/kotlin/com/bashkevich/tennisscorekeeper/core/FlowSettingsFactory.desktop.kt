package com.bashkevich.tennisscorekeeper.core

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import java.util.prefs.Preferences

@OptIn(ExperimentalSettingsApi::class)
internal actual class FlowSettingsFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual fun createSettings(): FlowSettings {
        return PreferencesSettings(Preferences.userRoot()).toFlowSettings()
    }
}