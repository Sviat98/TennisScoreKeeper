package com.bashkevich.tennisscorekeeper.core.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppLanguage
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.get

class  KeyValueStorage(
    private val dataStore: DataStore<Preferences>
) {
    private val PLAYER_ID_KEY = stringPreferencesKey("playerId")
    private val PLAYER_NAME_KEY = stringPreferencesKey("playerName")
    private val PLAYER_SURNAME_KEY = stringPreferencesKey("playerSurname")
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
    private val APP_THEME_KEY = stringPreferencesKey("appTheme")
    private val APP_LANGUAGE_KEY = stringPreferencesKey("appLocale")


    private val STRING_DEFAULT = ""

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    fun observeTokens(): Flow<Pair<String, String>> =
        dataStore.data.map {
            Pair(it[ACCESS_TOKEN_KEY] ?: STRING_DEFAULT, it[REFRESH_TOKEN_KEY] ?: STRING_DEFAULT)
        }

    suspend fun savePlayerId(playerId: String) {
        dataStore.edit { it[PLAYER_ID_KEY] = playerId }
    }

    fun observePlayerId(): Flow<String> =
        dataStore.data.map { it[PLAYER_ID_KEY] ?: STRING_DEFAULT }

    suspend fun savePlayerName(name: String) {
        dataStore.edit { it[PLAYER_NAME_KEY] = name }
    }

    fun observePlayerName(): Flow<String> =
        dataStore.data.map { it[PLAYER_NAME_KEY] ?: STRING_DEFAULT }

    suspend fun savePlayerSurname(surname: String) {
        dataStore.edit { it[PLAYER_SURNAME_KEY] = surname }
    }

    fun observePlayerSurname(): Flow<String> =
        dataStore.data.map { it[PLAYER_SURNAME_KEY] ?: STRING_DEFAULT }

    fun observeRefreshToken(): Flow<String> =
        dataStore.data.map { it[REFRESH_TOKEN_KEY] ?: STRING_DEFAULT }

    suspend fun saveAppThemeMode(mode: AppThemeMode) {
        println("saveAppThemeMode $mode")
        dataStore.edit {
            it[APP_THEME_KEY] = mode.name
        }
    }

    fun observeAppThemeMode(): Flow<AppThemeMode> {

        return dataStore.data.map {
            println("observeAppThemeMode value = ${it[APP_THEME_KEY]}")
            println("observeAppThemeMode name = ${AppThemeMode.fromName(it[APP_THEME_KEY])}")

            AppThemeMode.fromName(it[APP_THEME_KEY])
        }
    }

    suspend fun saveAppLanguage(language: AppLanguage) {
        dataStore.edit {
            it[APP_LANGUAGE_KEY] = language.name
        }
    }

    fun observeAppLanguage(): Flow<AppLanguage> =
        dataStore.data.map { AppLanguage.fromName(it[APP_LANGUAGE_KEY]) }

}
