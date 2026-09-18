package com.bashkevich.tennisscorekeeper.model.auth.local

import com.bashkevich.tennisscorekeeper.core.local.KeyValueStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

class AuthLocalDataSource(
    private val keyValueStorage: KeyValueStorage
) {
    fun observeUserId(): Flow<String> = keyValueStorage.observeUserId()
    suspend fun saveLoggedInUser(userId: String, name: String, surname: String) {
        keyValueStorage.saveUserId(userId)
        keyValueStorage.saveUserName(name)
        keyValueStorage.saveUserSurname(surname)
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        keyValueStorage.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    suspend fun getRefreshToken(): String {
       return keyValueStorage.observeRefreshToken().distinctUntilChanged().first()
    }

    fun observeUserName(): Flow<String> = keyValueStorage.observeUserName()

    fun observeUserSurname(): Flow<String> = keyValueStorage.observeUserSurname()
}
