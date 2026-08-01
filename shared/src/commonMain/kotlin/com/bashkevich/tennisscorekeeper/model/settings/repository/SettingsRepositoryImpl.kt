package com.bashkevich.tennisscorekeeper.model.settings.repository

import com.bashkevich.tennisscorekeeper.core.local.KeyValueStorage
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val keyValueStorage: KeyValueStorage,
) : SettingsRepository {

    override fun observeAppThemeMode(): Flow<AppThemeMode> =
        keyValueStorage.observeAppThemeMode()

    override suspend fun saveAppThemeMode(mode: AppThemeMode) {
        keyValueStorage.saveAppThemeMode(mode)
    }
}
