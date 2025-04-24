package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.BASE_URL_BACKEND
import com.bashkevich.tennisscorekeeper.core.BASE_URL_LOCAL_BACKEND
import com.bashkevich.tennisscorekeeper.core.httpClient
import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.counter.repository.CounterRepository
import com.bashkevich.tennisscorekeeper.model.counter.repository.CounterRepositoryImpl
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogViewModel
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListViewModel
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListViewModel
import io.ktor.client.plugins.DefaultRequest
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val counterModule = module {
    viewModelOf(::CounterListViewModel)
    viewModelOf(::AddCounterDialogViewModel)
    viewModelOf(::CounterDetailsViewModel)

    singleOf(::CounterRepositoryImpl) {
        bind<CounterRepository>()
    }
    singleOf(::CounterRemoteDataSource)
}

val matchModule = module {
    viewModelOf(::MatchListViewModel)
    //viewModelOf(::AddCounterDialogViewModel)
    viewModelOf(::MatchDetailsViewModel)
    singleOf(::MatchRepositoryImpl) {
        bind<MatchRepository>()
    }
    singleOf(::MatchRemoteDataSource)
}

val coreModule = module {
    val jsonSerializer = Json {
        prettyPrint = true
        isLenient = true
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

expect val platformModule: Module