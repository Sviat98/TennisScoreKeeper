package com.bashkevich.tennisscorekeeper

import io.ktor.http.Url
import io.ktor.http.URLProtocol

data class AppConfig(
    val baseUrlFrontend: String,
    val baseUrlBackend: String,
) {
    private val parsedBackendUrl: Url = Url(baseUrlBackend)

    val backendProtocol: URLProtocol = parsedBackendUrl.protocol

    val backendHost: String = parsedBackendUrl.host

    // Url.port сам подставляет protocol.defaultPort, когда порт не указан
    // («не указан» в Ktor = DEFAULT_PORT = 0), поэтому для https/http всегда 443/80
    val backendPort: Int = parsedBackendUrl.port

    val backendWsProtocol: URLProtocol =
        if (parsedBackendUrl.protocol == URLProtocol.HTTPS) URLProtocol.WSS else URLProtocol.WS

    companion object{
        val current: AppConfig by lazy {
            AppConfig(
                baseUrlFrontend = BuildConfig.frontendUrl,
                baseUrlBackend = BuildConfig.backendUrl,
            )
        }

        fun logBuildMode() {
            val buildMode = getBuildMode()
            println("Current build mode: $buildMode")
        }

        private fun getBuildMode(): BuildMode = BuildMode.valueOf(BuildConfig.buildMode)
    }
}

enum class BuildMode{
    DEBUG,RELEASE
}
