package com.bashkevich.tennisscorekeeper.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings


internal actual class FlowSettingsFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {
    private val Context.datastore : DataStore<Preferences> by preferencesDataStore("tennis_score_keeper")
    @OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
    actual fun createSettings(): FlowSettings {

        val dataStore = platformConfiguration.androidContext.datastore
        return DataStoreSettings(dataStore)
    }
}