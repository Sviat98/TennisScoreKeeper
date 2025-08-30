package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.AppViewModel
import com.bashkevich.tennisscorekeeper.core.BASE_URL_BACKEND
import com.bashkevich.tennisscorekeeper.core.FlowSettingsFactory
import com.bashkevich.tennisscorekeeper.core.KeyValueStorage
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import com.bashkevich.tennisscorekeeper.core.httpClient
import com.russhwolf.settings.ExperimentalSettingsApi
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
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
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

        val client = httpClient {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL_BACKEND
                }
                contentType(ContentType.Application.Json)
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
                        BearerTokens("abc123", "def456")
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