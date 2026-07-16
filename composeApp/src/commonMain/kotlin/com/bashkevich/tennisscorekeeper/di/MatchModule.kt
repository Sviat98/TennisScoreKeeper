package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.match.local.MatchLocalDataSource
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchViewModel
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val matchModule = module {
    viewModel<MatchDetailsViewModel>()
    viewModel<AddMatchViewModel>()

    single<MatchRepositoryImpl>().bind(MatchRepository::class)
    single<MatchRemoteDataSource>()
    single<MatchLocalDataSource>()
}
