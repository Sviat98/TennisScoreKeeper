package com.bashkevich.tennisscorekeeper.model.auth.local

import com.bashkevich.tennisscorekeeper.core.KeyValueStorage
import kotlinx.coroutines.flow.Flow

class AuthLocalDataSource(
    private val keyValueStorage: KeyValueStorage
) {
    fun observePlayerId(): Flow<String> = keyValueStorage.observePlayerId()
    suspend fun savePlayerId(playerId: String) {
        keyValueStorage.savePlayerId(playerId)
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        keyValueStorage.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }
}