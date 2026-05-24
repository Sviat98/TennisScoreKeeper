package com.bashkevich.tennisscorekeeper.core.local

internal actual class FlowSettingsFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {
    private val Context.datastore : DataStore<Preferences> by preferencesDataStore("tennis_score_keeper")
    @OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
    actual fun createSettings(): FlowSettings {

        val dataStore = platformConfiguration.androidContext.datastore
        return DataStoreSettings(dataStore)
    }
}