package com.bashkevich.tennisscorekeeper.core

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class KeyValueStorage @OptIn(ExperimentalSettingsApi::class) constructor(
    private val flowSettings: FlowSettings
) {
    private val PLAYER_ID_KEY = "playerId"
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
}