package com.bashkevich.tennisscorekeeper.core

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable

@OptIn(ExperimentalSettingsApi::class)
internal actual class FlowSettingsFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual fun createSettings(): FlowSettings {
        return StorageSettings().makeObservable().toFlowSettings()
    }
}