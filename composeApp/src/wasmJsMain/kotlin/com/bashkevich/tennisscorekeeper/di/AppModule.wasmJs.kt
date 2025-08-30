package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import com.bashkevich.tennisscorekeeper.screens.scoreboard.ScoreboardViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::PlatformConfiguration)
    viewModelOf(::ScoreboardViewModel)
}