package com.bashkevich.tennisscorekeeper.model.auth.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginBody
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginResponseDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observePlayerId(): Flow<String>
    suspend fun savePlayerId(playerId: String)
    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun login(loginBody: LoginBody): LoadResult<LoginResponseDto, Throwable>
    suspend fun logout()
    suspend fun checkRefreshTokenStatus(): LoadResult<ResponseMessage, Throwable>
}