package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import com.bashkevich.tennisscorekeeper.screens.scoreboard.ScoreboardViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

actual val platformModule = module {
    singleOf(::PlatformConfiguration)
    viewModel<ScoreboardViewModel>()
}