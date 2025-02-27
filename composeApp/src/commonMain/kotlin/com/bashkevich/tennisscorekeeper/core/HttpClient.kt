package com.bashkevich.tennisscorekeeper.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val BASE_URL_FRONTEND = "https://tennisscorekeeper.onrender.com/"

const val BASE_URL_LOCAL_BACKEND = "http://localhost:8080/"

const val BASE_URL_BACKEND = "tennisscorekeeperbackend.onrender.com"

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient