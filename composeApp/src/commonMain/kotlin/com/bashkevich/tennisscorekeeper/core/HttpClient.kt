package com.bashkevich.tennisscorekeeper.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val BASE_URL_FRONTEND_DEBUG = "https://tennisscorekeeper.onrender.com"

const val BASE_URL_FRONTEND_RELEASE = ""

const val BASE_URL_LOCAL_BACKEND = "http://localhost:8080/"

const val BASE_URL_BACKEND_DEBUG = "tennisscorekeeperbackend.onrender.com"

const val BASE_URL_BACKEND_RELEASE = ""

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient