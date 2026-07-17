package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeLocalDataSource
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.settings.themelist.ScoreboardThemeListViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.themedetails.ScoreboardThemeDetailsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val themeModule = module {
    single<ThemeRepositoryImpl>().bind(ThemeRepository::class)
    single<ThemeRemoteDataSource>()
    single<ThemeLocalDataSource>()
    viewModel<ScoreboardThemeListViewModel>()
    viewModel<ScoreboardThemeDetailsViewModel>()
}
