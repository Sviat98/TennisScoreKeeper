package com.bashkevich.tennisscorekeeper

/**
 * На Android режим приходит из product flavor (dev/prod) хост-приложения через
 * [AppConfig.setBuildMode] в MainActivity, поэтому своего источника здесь нет.
 */
internal actual fun platformBuildMode(): BuildMode? = null
