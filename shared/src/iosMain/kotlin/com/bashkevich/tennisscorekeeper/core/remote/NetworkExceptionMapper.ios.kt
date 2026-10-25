package com.bashkevich.tennisscorekeeper.core.remote

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorSecureConnectionFailed
import platform.Foundation.NSURLErrorTimedOut

// NSURLSession reports every transport-level failure as an NSError wrapped by the Darwin
// engine; only the connectivity-related codes count as "no network" for the UI.
private val NETWORK_ERROR_CODES = setOf(
    NSURLErrorNotConnectedToInternet,
    NSURLErrorNetworkConnectionLost,
    NSURLErrorCannotFindHost,
    NSURLErrorCannotConnectToHost,
    NSURLErrorDNSLookupFailed,
    NSURLErrorTimedOut,
    NSURLErrorSecureConnectionFailed,
)

actual fun Throwable.toNetworkException(): NetworkException? {
    val darwinException = this as? DarwinHttpRequestException
        ?: this.cause as? DarwinHttpRequestException
        ?: return null

    return if (darwinException.origin.code in NETWORK_ERROR_CODES) {
        NetworkException(darwinException)
    } else null
}
