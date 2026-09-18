package com.bashkevich.tennisscorekeeper.model.auth.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.model.auth.domain.LoggedInUser
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginBody
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginResponseDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeUserId(): Flow<String>
    fun observeLoggedInUser(): Flow<LoggedInUser>
    suspend fun saveLoggedInUser(userId: String, name: String, surname: String)
    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun login(loginBody: LoginBody): LoadResult<LoginResponseDto, Throwable>
    suspend fun logout()
    suspend fun checkRefreshTokenStatus(): LoadResult<ResponseMessage, Throwable>
}
