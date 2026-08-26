package com.bashkevich.tennisscorekeeper

import platform.Foundation.NSBundle

/**
 * На iOS режим приходит из build setting BUILD_MODE, подставленной в Info.plist
 * (ключ BuildMode). Конфигурации Debug/Release-Dev дают DEBUG, Debug-Prod/Release — RELEASE.
 */
internal actual fun platformBuildMode(): BuildMode? =
    parseBuildMode(NSBundle.mainBundle.objectForInfoDictionaryKey("BuildMode") as? String)
