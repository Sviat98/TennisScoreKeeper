package com.bashkevich.tennisscorekeeper.model.auth.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.auth.local.AuthLocalDataSource
import com.bashkevich.tennisscorekeeper.model.auth.remote.AuthRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginBody
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginResponseDto
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {
    override fun observePlayerId(): Flow<String> = authLocalDataSource.observePlayerId()
    override suspend fun savePlayerId(playerId: String) {
        authLocalDataSource.savePlayerId(playerId)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        authLocalDataSource.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    override suspend fun login(loginBody: LoginBody): LoadResult<LoginResponseDto, Throwable> {
        return authRemoteDataSource.login(loginBody).doOnSuccess { loginResponseDto ->
            authLocalDataSource.savePlayerId(loginResponseDto.playerId)
            authLocalDataSource.saveTokens(
                loginResponseDto.accessToken,
                loginResponseDto.refreshToken
            )
        }
    }
}