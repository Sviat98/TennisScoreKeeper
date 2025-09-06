package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.auth.local.AuthLocalDataSource
import com.bashkevich.tennisscorekeeper.model.auth.remote.AuthRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.login.LoginViewModel
import com.bashkevich.tennisscorekeeper.screens.profile.ProfileViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    singleOf(::AuthRemoteDataSource)
    singleOf(::AuthLocalDataSource)
    singleOf(::AuthRepositoryImpl){
        bind<AuthRepository>()
    }
    viewModelOf(::LoginViewModel)
    viewModelOf(::ProfileViewModel)
}