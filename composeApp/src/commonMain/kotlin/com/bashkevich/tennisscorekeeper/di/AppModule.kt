package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.httpClient
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListViewModel
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val counterModule = module {
    viewModelOf(::CounterListViewModel)
    viewModelOf(::CounterDetailsViewModel)
}

val coreModule = module {
    val jsonSerializer = Json {
        prettyPrint = true
        isLenient = true
    }

    single {
        httpClient {
            install(ContentNegotiation) {
                json(jsonSerializer)
            }
            install(WebSockets) {
                pingIntervalMillis = 20_000
                contentConverter = KotlinxWebsocketSerializationConverter(jsonSerializer)
            }
        }
    }
}

expect val platformModule: Module