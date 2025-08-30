package com.bashkevich.tennisscorekeeper.core

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings

internal expect class FlowSettingsFactory(platformConfiguration: PlatformConfiguration) {
    @OptIn(ExperimentalSettingsApi::class)
    fun createSettings(): FlowSettings
}