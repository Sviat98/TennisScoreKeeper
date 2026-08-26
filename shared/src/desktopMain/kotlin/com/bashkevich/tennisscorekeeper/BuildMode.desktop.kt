package com.bashkevich.tennisscorekeeper

/**
 * Десктоп переключается без пересборки: `-DBUILD_MODE=RELEASE` в аргументах JVM
 * или переменная окружения BUILD_MODE.
 */
internal actual fun platformBuildMode(): BuildMode? =
    parseBuildMode(System.getProperty("BUILD_MODE"))
        ?: parseBuildMode(System.getenv("BUILD_MODE"))
