package com.bashkevich.tennisscorekeeper.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val BASE_URL_FRONTEND = "https://tennisscorekeeper.onrender.com/"

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient