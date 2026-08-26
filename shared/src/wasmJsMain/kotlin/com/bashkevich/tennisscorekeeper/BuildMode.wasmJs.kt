package com.bashkevich.tennisscorekeeper

import kotlinx.browser.window

/**
 * В браузере переключается прямо в адресной строке: `?buildMode=release`.
 * Удобно, чтобы дев-сборкой постучаться в прод-бэкенд без пересборки wasm-бандла.
 */
internal actual fun platformBuildMode(): BuildMode? = runCatching {
    val search = window.location.search
    val param = search.removePrefix("?")
        .split("&")
        .firstOrNull { it.startsWith("buildMode=", ignoreCase = true) }
        ?.substringAfter("=")
    parseBuildMode(param)
}.getOrNull()
