package com.bashkevich.tennisscorekeeper.core.remote

import io.ktor.client.engine.js.JsError

actual fun Throwable.toNetworkException(): NetworkException? =
    if (this is JsError) NetworkException(this) else null
