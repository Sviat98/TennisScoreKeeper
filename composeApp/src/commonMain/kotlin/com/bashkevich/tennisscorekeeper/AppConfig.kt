package com.bashkevich.tennisscorekeeper

import com.bashkevich.tennisscorekeeper.core.BASE_HOST_BACKEND_DEBUG
import com.bashkevich.tennisscorekeeper.core.BASE_HOST_BACKEND_RELEASE
import com.bashkevich.tennisscorekeeper.core.BASE_URL_FRONTEND_DEBUG
import com.bashkevich.tennisscorekeeper.core.BASE_URL_FRONTEND_RELEASE

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

        fun logBuildMode() {
            val buildMode = getBuildMode()
            println("Current build mode: $buildMode")
        }

        private fun getBuildMode(): BuildMode = BuildConfig.buildMode

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