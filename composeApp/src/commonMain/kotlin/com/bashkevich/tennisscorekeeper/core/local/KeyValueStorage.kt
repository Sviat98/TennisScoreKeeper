package com.bashkevich.tennisscorekeeper.core.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class KeyValueStorage @OptIn(ExperimentalSettingsApi::class) constructor(
    private val flowSettings: FlowSettings
) {
    private val PLAYER_ID_KEY = "playerId"
    private val PLAYER_NAME_KEY = "playerName"
    private val PLAYER_SURNAME_KEY = "playerSurname"
    private val ACCESS_TOKEN_KEY = "accessToken"
    private val REFRESH_TOKEN_KEY = "refreshToken"


    private val STRING_DEFAULT = ""

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        flowSettings.putString(ACCESS_TOKEN_KEY, accessToken)
        flowSettings.putString(REFRESH_TOKEN_KEY, refreshToken)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun observeTokens(): Flow<Pair<String, String>> {
        val accessTokenFlow = flowSettings.getStringFlow(ACCESS_TOKEN_KEY, STRING_DEFAULT)
        val refreshTokenFlow = flowSettings.getStringFlow(REFRESH_TOKEN_KEY, STRING_DEFAULT)

        return accessTokenFlow.combine(refreshTokenFlow) { accessToken, refreshToken->
            Pair(accessToken,refreshToken)
        }
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun savePlayerId(playerId: String) {
        flowSettings.putString(PLAYER_ID_KEY, playerId)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun observePlayerId() = flowSettings.getStringFlow(PLAYER_ID_KEY, STRING_DEFAULT)

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun savePlayerName(name: String) {
        flowSettings.putString(PLAYER_NAME_KEY, name)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun observePlayerName() = flowSettings.getStringFlow(PLAYER_NAME_KEY, STRING_DEFAULT)

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun savePlayerSurname(surname: String) {
        flowSettings.putString(PLAYER_SURNAME_KEY, surname)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun observePlayerSurname() = flowSettings.getStringFlow(PLAYER_SURNAME_KEY, STRING_DEFAULT)

    @OptIn(ExperimentalSettingsApi::class)
    fun observeRefreshToken() = flowSettings.getStringFlow(REFRESH_TOKEN_KEY, STRING_DEFAULT)
}