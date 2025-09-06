package com.bashkevich.tennisscorekeeper.model.auth.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters

class AuthRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun login(loginBody: LoginBody): LoadResult<LoginResponseDto, Throwable> {
        return runOperationCatching {
            val loginResponseDto = httpClient.post("/login") {
                setBody(loginBody)
            }.body<LoginResponseDto>()

            loginResponseDto
        }
    }

    suspend fun refreshTokenStatus(refreshToken: String): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val refreshTokenStatusMessage = httpClient.submitForm(
                url = "/refreshTokenStatus",
                formParameters = parameters {
                    append("refreshToken", refreshToken)
                }) {
            }.body<ResponseMessage>()

            refreshTokenStatusMessage
        }
    }

    suspend fun logout(refreshToken: String): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val logoutMessage = httpClient.submitForm(
                url = "/logout",
                formParameters = parameters {
                    append("refreshToken", refreshToken)
                }) {
            }.body<ResponseMessage>()

            logoutMessage
        }
    }
}