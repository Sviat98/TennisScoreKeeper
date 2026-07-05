package com.bashkevich.tennisscorekeeper.core.remote

import io.ktor.client.engine.js.JsError

@OptIn(ExperimentalWasmJsInterop::class)
actual fun Throwable.toNetworkException(): NetworkException? {
    val exception = this.cause

    return if (exception is JsError
        && exception.origin.asJsException().message == "Failed to fetch"){
        NetworkException(exception)
    } else null
}