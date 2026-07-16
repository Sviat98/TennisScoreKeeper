package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.auth.local.AuthLocalDataSource
import com.bashkevich.tennisscorekeeper.model.auth.remote.AuthRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.login.LoginViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.main.SettingsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val authModule = module {
    single<AuthRemoteDataSource>()
    single<AuthLocalDataSource>()
    single<AuthRepositoryImpl>().bind(AuthRepository::class)
    viewModel<LoginViewModel>()
    viewModel<SettingsViewModel>()
}
