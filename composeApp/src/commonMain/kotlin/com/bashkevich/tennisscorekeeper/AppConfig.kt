package com.bashkevich.tennisscorekeeper

import com.bashkevich.tennisscorekeeper.core.BASE_URL_BACKEND_DEBUG
import com.bashkevich.tennisscorekeeper.core.BASE_URL_BACKEND_RELEASE
import com.bashkevich.tennisscorekeeper.core.BASE_URL_FRONTEND_DEBUG
import com.bashkevich.tennisscorekeeper.core.BASE_URL_FRONTEND_RELEASE

data class AppConfig(
    val baseUrlFrontend: String,
    val baseUrlBackend: String
){
    companion object{
        fun getBuildMode() = BuildMode.DEBUG

        fun getCurrentConfig(buildMode: BuildMode)= when(buildMode){
            BuildMode.DEBUG->{
                AppConfig(
                    baseUrlFrontend = BASE_URL_FRONTEND_DEBUG,
                    baseUrlBackend = BASE_URL_BACKEND_DEBUG
                )
            }
            BuildMode.RELEASE -> {
                AppConfig(
                    baseUrlFrontend = BASE_URL_FRONTEND_RELEASE,
                    baseUrlBackend = BASE_URL_BACKEND_RELEASE
                )
            }
        }
    }
}

enum class BuildMode{
    DEBUG,RELEASE
}