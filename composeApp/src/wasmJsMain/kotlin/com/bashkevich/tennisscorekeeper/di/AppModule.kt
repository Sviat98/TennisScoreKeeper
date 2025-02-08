package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.screens.counteroverlay.CounterOverlayViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule = module {
    viewModelOf(::CounterOverlayViewModel)
}