package com.bashkevich.tennisscorekeeper.model.auth

import com.bashkevich.tennisscorekeeper.core.KeyValueStorage
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
  private val keyValueStorage: KeyValueStorage
) : AuthRepository {
    override fun observePlayerId(): Flow<String> = keyValueStorage.observePlayerId()
    override suspend fun savePlayerId(playerId: String) {
        keyValueStorage.savePlayerId(playerId)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        keyValueStorage.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }
}