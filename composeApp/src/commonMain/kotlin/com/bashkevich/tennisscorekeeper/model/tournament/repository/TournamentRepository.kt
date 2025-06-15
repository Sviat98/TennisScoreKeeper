package com.bashkevich.tennisscorekeeper.model.tournament.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import kotlinx.coroutines.flow.SharedFlow

interface TournamentRepository {
    suspend fun getTournaments(): LoadResult<List<Tournament>, Throwable>
    suspend fun addTournament(addTournamentBody: AddTournamentBody): LoadResult<Tournament, Throwable>
    fun emitNewTournament(addTournamentBody: AddTournamentBody)
    fun observeNewTournament(): SharedFlow<AddTournamentBody>
    suspend fun getTournamentById(id: String): LoadResult<Tournament, Throwable>
}