package com.bashkevich.tennisscorekeeper.core.local

@OptIn(ExperimentalSettingsApi::class)
internal actual class FlowSettingsFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual fun createSettings(): FlowSettings {
        return StorageSettings().makeObservable().toFlowSettings()
    }
}