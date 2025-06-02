package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.counter.repository.CounterRepository
import com.bashkevich.tennisscorekeeper.model.counter.repository.CounterRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogViewModel
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

val counterModule = module {
    viewModelOf(::CounterListViewModel)
    viewModelOf(::AddCounterDialogViewModel)
    viewModelOf(::CounterDetailsViewModel)

    singleOf(::CounterRepositoryImpl) {
        bind<CounterRepository>()
    }
    singleOf(::CounterRemoteDataSource)
}