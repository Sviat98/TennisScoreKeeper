package com.bashkevich.tennisscorekeeper

import com.bashkevich.tennisscorekeeper.core.remote.BASE_HOST_BACKEND_DEBUG
import com.bashkevich.tennisscorekeeper.core.remote.BASE_HOST_BACKEND_RELEASE
import com.bashkevich.tennisscorekeeper.core.remote.BASE_URL_FRONTEND_DEBUG
import com.bashkevich.tennisscorekeeper.core.remote.BASE_URL_FRONTEND_RELEASE

data class AppConfig(
    val baseUrlFrontend: String,
    val baseHostBackend: String
){
    companion object{
        private val debugConfig: AppConfig by lazy {
            AppConfig(BASE_URL_FRONTEND_DEBUG, BASE_HOST_BACKEND_DEBUG)
        }

        private val releaseConfig: AppConfig by lazy {
            AppConfig(BASE_URL_FRONTEND_RELEASE, BASE_HOST_BACKEND_RELEASE)
        }

        private var overriddenBuildMode: BuildMode? = null

        /**
         * Явно задаёт режим из точки входа платформы. Вызывать ДО первого обращения к [current]
         * (на Android — в MainActivity.onCreate до setContent). Нужен там, где режим известен
         * только хост-приложению: например, Android product flavor dev/prod.
         */
        fun setBuildMode(mode: BuildMode) {
            overriddenBuildMode = mode
        }

        /**
         * Приоритет источников режима (по убыванию):
         * 1. [setBuildMode] — flavor хост-приложения (Android);
         * 2. [platformBuildMode] — платформенный источник (Info.plist на iOS,
         *    -DBUILD_MODE на десктопе, ?buildMode= в URL на вебе);
         * 3. BuildKonfig — гредловый BUILD_MODE, зашитый в момент сборки.
         */
        fun getBuildMode(): BuildMode =
            overriddenBuildMode
                ?: platformBuildMode()
                ?: BuildMode.valueOf(BuildConfig.buildMode)

        fun logBuildMode() {
            println("Current build mode: ${getBuildMode()} -> ${current.baseHostBackend}")
        }

        val current: AppConfig
            get() =
                when (getBuildMode()) {
                    BuildMode.DEBUG -> debugConfig
                    BuildMode.RELEASE -> releaseConfig
                }
    }
}

enum class BuildMode{
    DEBUG,RELEASE
}

/** Разбирает строку в [BuildMode], игнорируя регистр. null — если значение пустое или неизвестное. */
internal fun parseBuildMode(raw: String?): BuildMode? =
    raw?.trim()?.takeIf { it.isNotEmpty() }?.let { value ->
        BuildMode.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }

/** Платформенный источник режима сборки. null — источник не задан, используется fallback. */
internal expect fun platformBuildMode(): BuildMode?
