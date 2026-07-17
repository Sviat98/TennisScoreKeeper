package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentLocalDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepositoryImpl
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.RefreshTournamentListScreenUseCase
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val tournamentModule = module {
    viewModel<TournamentListViewModel>()
    single<RefreshTournamentListScreenUseCase>()
    viewModel<TournamentViewModel>()
    viewModel<AddTournamentViewModel>()

    single<TournamentRepositoryImpl>().bind(TournamentRepository::class)
    single<TournamentRemoteDataSource>()
    single<TournamentLocalDataSource>()
}
