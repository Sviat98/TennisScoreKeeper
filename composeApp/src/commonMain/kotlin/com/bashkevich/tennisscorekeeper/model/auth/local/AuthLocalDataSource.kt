package com.bashkevich.tennisscorekeeper.model.auth.local

import com.bashkevich.tennisscorekeeper.core.local.KeyValueStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

class AuthLocalDataSource(
    private val keyValueStorage: KeyValueStorage
) {
    fun observePlayerId(): Flow<String> = keyValueStorage.observePlayerId()
    suspend fun saveLoggedInPlayer(playerId: String, name: String, surname: String) {
        keyValueStorage.savePlayerId(playerId)
        keyValueStorage.savePlayerName(name)
        keyValueStorage.savePlayerSurname(surname)
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        keyValueStorage.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    suspend fun getRefreshToken(): String {
       return keyValueStorage.observeRefreshToken().distinctUntilChanged().first()
    }

    fun observePlayerName(): Flow<String> = keyValueStorage.observePlayerName()

    fun observePlayerSurname(): Flow<String> = keyValueStorage.observePlayerSurname()
}