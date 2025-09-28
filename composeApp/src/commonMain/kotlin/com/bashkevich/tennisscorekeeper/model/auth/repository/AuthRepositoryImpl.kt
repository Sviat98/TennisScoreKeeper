package com.bashkevich.tennisscorekeeper.model.auth.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.UnauthorizedException
import com.bashkevich.tennisscorekeeper.core.doOnError
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

    override suspend fun checkRefreshTokenStatus() : LoadResult<ResponseMessage, Throwable>{
        val refreshToken = authLocalDataSource.getRefreshToken()

        val result = if(refreshToken.isNotEmpty()){
            authRemoteDataSource.refreshTokenStatus(refreshToken).doOnError { throwable ->
                if (throwable is UnauthorizedException){
                    authLocalDataSource.savePlayerId("")
                    authLocalDataSource.saveTokens(accessToken = "", refreshToken = "")
                }
            }
        }else{
            LoadResult.Error(UnauthorizedException("Refresh token is empty!"))
        }

        return result
    }

    override suspend fun logout() {
        val refreshToken = authLocalDataSource.getRefreshToken()
        // удаляем данные в ЛЮБОМ случае
        authLocalDataSource.savePlayerId("")
        authLocalDataSource.saveTokens(accessToken = "", refreshToken = "")
        authRemoteDataSource.logout(refreshToken)
    }
}