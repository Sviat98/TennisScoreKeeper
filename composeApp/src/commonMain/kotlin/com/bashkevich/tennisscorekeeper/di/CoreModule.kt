package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.AppViewModel
import com.bashkevich.tennisscorekeeper.core.FlowSettingsFactory
import com.bashkevich.tennisscorekeeper.core.KeyValueStorage
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.UnauthorizedException
import com.bashkevich.tennisscorekeeper.core.doOnError
import com.bashkevich.tennisscorekeeper.core.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.httpClient
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginResponseDto
import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val coreModule = module {

    single {
        val platformConfiguration = get<PlatformConfiguration>()
        FlowSettingsFactory(platformConfiguration).createSettings()
    }
    singleOf(::KeyValueStorage) {
        createdAtStart()
    }

    val jsonSerializer = Json {
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    single {
        val keyValueStorage = get<KeyValueStorage>()

        val appConfig = AppConfig.current

        val client = httpClient {
            expectSuccess = true
            HttpResponseValidator {
                handleResponseException { exception, request ->
                    val clientException =
                        exception as? ClientRequestException ?: return@handleResponseException
                    val exceptionResponse = clientException.response
                    if (exceptionResponse.status == HttpStatusCode.Unauthorized) {
                        val exceptionResponseText =
                            exceptionResponse.body<ResponseMessage>().message
                        throw UnauthorizedException(exceptionResponseText)
                    }
                }
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = appConfig.baseHostBackend
                }
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Origin, appConfig.baseUrlFrontend)
            }
            install(Logging) {
                level = LogLevel.ALL
                sanitizeHeader { header ->
                    header == HttpHeaders.Authorization
                }
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val (accessToken, refreshToken) = keyValueStorage.observeTokens()
                            .distinctUntilChanged()
                            .first()

                        val bearerTokens =
                            if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) BearerTokens(
                                accessToken,
                                refreshToken
                            ) else null

                        bearerTokens
                    }
                    refreshTokens {
                        println("refreshTokens TRIGGER")
                        val refreshToken = oldTokens?.refreshToken

                        var bearerTokens: BearerTokens? = null

                        refreshToken?.let {
                            runOperationCatching {
                                client.submitForm(
                                    url = "/refreshToken",
                                    formParameters = parameters {
                                        append("refreshToken", refreshToken)
                                    }) {
                                    markAsRefreshTokenRequest()
                                }.body<LoginResponseDto>()
                            }.doOnSuccess { loginResponseDto ->
                                val accessToken = loginResponseDto.accessToken
                                val refreshToken = loginResponseDto.refreshToken

                                keyValueStorage.saveTokens(accessToken, refreshToken)
                                bearerTokens = BearerTokens(accessToken, refreshToken)
                            }.doOnError { throwable ->
                                if (throwable is UnauthorizedException) {
                                    keyValueStorage.savePlayerId("")
                                    keyValueStorage.saveTokens("", "")
                                }
                            }
                        }

                        bearerTokens
                    }
                }
            }
            install(ContentNegotiation) {
                json(jsonSerializer)
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000
            }
            install(WebSockets) {
                pingIntervalMillis = 15_000
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
        client.plugin(HttpSend).intercept { request ->
            val method = request.method
            val newRequest = when {
                method !in listOf(HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch) -> {
                    // Для GET, DELETE и других методов не добавляем токен
                    request.headers.remove(HttpHeaders.Authorization)
                    request
                }

                else -> {
                    request
                }
            }
            execute(newRequest)
        }
        client
    }
    viewModelOf(::AppViewModel)
}