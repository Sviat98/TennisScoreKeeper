package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import org.koin.dsl.module

actual val platformModule = module {
    single {
        PlatformConfiguration(get())
    }
}