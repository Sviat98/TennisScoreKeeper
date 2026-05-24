package com.bashkevich.tennisscorekeeper.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val BASE_URL_FRONTEND_DEBUG = "https://tennisscorekeeper.onrender.com"

const val BASE_URL_FRONTEND_RELEASE = "https://tennisscorekeeper.tech"

const val BASE_URL_LOCAL_BACKEND = "http://localhost:8080/"

const val BASE_HOST_BACKEND_DEBUG = "tennisscorekeeperbackend.onrender.com"

const val BASE_HOST_BACKEND_RELEASE = "api.tennisscorekeeper.tech"

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient