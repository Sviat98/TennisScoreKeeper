package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::PlatformConfiguration)
}