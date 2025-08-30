package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.auth.AuthRepository
import com.bashkevich.tennisscorekeeper.model.auth.AuthRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.login.LoginViewModel
import com.bashkevich.tennisscorekeeper.screens.profile.ProfileViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    singleOf(::AuthRepositoryImpl){
        bind<AuthRepository>()
    }
    viewModelOf(::LoginViewModel)
    viewModelOf(::ProfileViewModel)
}