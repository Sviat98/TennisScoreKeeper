package com.bashkevich.tennisscorekeeper.core.local

import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings

internal expect class FlowSettingsFactory(platformConfiguration: PlatformConfiguration) {
    @OptIn(ExperimentalSettingsApi::class)
    fun createSettings(): FlowSettings
}