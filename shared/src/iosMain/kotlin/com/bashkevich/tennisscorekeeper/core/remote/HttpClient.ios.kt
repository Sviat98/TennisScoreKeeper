package com.bashkevich.tennisscorekeeper.core.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    config(this)
    engine {
        configureRequest {
            // Keeps idle WebSocket sessions alive long enough for the application-level
            // heartbeat (30 s silence + 10 s reply window) to run its course.
            setTimeoutInterval(60.0)
        }
    }
}
