package com.bashkevich.tennisscorekeeper.model.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observePlayerId(): Flow<String>
    suspend fun savePlayerId(playerId: String)
    suspend fun saveTokens(accessToken: String, refreshToken: String)
}