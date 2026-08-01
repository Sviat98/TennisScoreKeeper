package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.settings.repository.SettingsRepository
import com.bashkevich.tennisscorekeeper.model.settings.repository.SettingsRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.settings.general.GeneralSettingsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val settingsModule = module {
    single<SettingsRepositoryImpl>().bind(SettingsRepository::class)
    viewModel<GeneralSettingsViewModel>()
}
