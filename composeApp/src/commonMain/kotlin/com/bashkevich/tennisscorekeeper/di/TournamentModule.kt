package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tournamentModule = module {
    viewModelOf(::TournamentListViewModel)
    viewModelOf(::AddTournamentViewModel)

    singleOf(::TournamentRepositoryImpl){
        bind<TournamentRepository>()
    }
    singleOf(::TournamentRemoteDataSource)
}