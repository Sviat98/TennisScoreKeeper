package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.BASE_URL_BACKEND
import com.bashkevich.tennisscorekeeper.core.httpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val coreModule = module {
    val jsonSerializer = Json {
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    single {
        httpClient {
            defaultRequest {
                url{
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL_BACKEND
                }
                contentType(ContentType.Application.Json)
            }
            install(Logging){
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(jsonSerializer)
            }
            install(WebSockets) {
                pingIntervalMillis = 15_000
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }
}