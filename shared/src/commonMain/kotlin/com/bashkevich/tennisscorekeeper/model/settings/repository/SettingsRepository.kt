package com.bashkevich.tennisscorekeeper.model.settings.repository

import com.bashkevich.tennisscorekeeper.model.settings.domain.AppLanguage
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeAppThemeMode(): Flow<AppThemeMode>
    suspend fun saveAppThemeMode(mode: AppThemeMode)

    fun observeAppLanguage(): Flow<AppLanguage>
    suspend fun saveAppLanguage(language: AppLanguage)
}
