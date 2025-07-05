package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.match.remote.MatchRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchViewModel
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val matchModule = module {
    viewModelOf(::MatchDetailsViewModel)
    viewModelOf(::AddMatchViewModel)

    singleOf(::MatchRepositoryImpl) {
        bind<MatchRepository>()
    }
    singleOf(::MatchRemoteDataSource)
}