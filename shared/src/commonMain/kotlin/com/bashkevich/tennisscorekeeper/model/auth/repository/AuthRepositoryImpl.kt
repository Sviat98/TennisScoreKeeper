package com.bashkevich.tennisscorekeeper.model.auth.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.auth.domain.LoggedInUser
import com.bashkevich.tennisscorekeeper.model.auth.local.AuthLocalDataSource
import com.bashkevich.tennisscorekeeper.model.auth.remote.AuthRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginBody
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {
    override fun observeUserId(): Flow<String> = authLocalDataSource.observeUserId()

    override fun observeLoggedInUser(): Flow<LoggedInUser> = combine(
        authLocalDataSource.observeUserId(),
        authLocalDataSource.observeUserName(),
        authLocalDataSource.observeUserSurname()
    ) { userId, name, surname ->
        LoggedInUser(userId = userId, name = name, surname = surname)
    }

    override suspend fun saveLoggedInUser(userId: String, name: String, surname: String) {
        authLocalDataSource.saveLoggedInUser(userId, name, surname)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        authLocalDataSource.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    override suspend fun login(loginBody: LoginBody): LoadResult<LoginResponseDto, Throwable> {
        return authRemoteDataSource.login(loginBody).doOnSuccess { loginResponseDto ->
            authLocalDataSource.saveLoggedInUser(
                loginResponseDto.user.id,
                loginResponseDto.user.name,
                loginResponseDto.user.surname
            )
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
                    authLocalDataSource.saveLoggedInUser("", "", "")
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
        authLocalDataSource.saveLoggedInUser("", "", "")
        authLocalDataSource.saveTokens(accessToken = "", refreshToken = "")
        authRemoteDataSource.clearAuthTokens()
        authRemoteDataSource.logout(refreshToken)
    }
}
