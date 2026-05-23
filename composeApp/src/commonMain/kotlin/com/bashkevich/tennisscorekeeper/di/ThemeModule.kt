package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeLocalDataSource
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val themeModule = module {
    singleOf(::ThemeRepositoryImpl) {
        bind<ThemeRepository>()
    }
    singleOf(::ThemeRemoteDataSource)
    singleOf(::ThemeLocalDataSource)
}
