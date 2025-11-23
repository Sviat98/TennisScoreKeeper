package com.bashkevich.tennisscorekeeper.model.tournament.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import kotlinx.coroutines.flow.SharedFlow

interface TournamentRepository {
    suspend fun getTournaments(): LoadResult<List<Tournament>, Throwable>
    suspend fun addTournament(addTournamentBody: AddTournamentBody): LoadResult<Tournament, Throwable>
    suspend fun changeTournamentStatus(tournamentId: String, tournamentStatus: TournamentStatus): LoadResult<ResponseMessage,Throwable>
    fun emitNewTournament(newTournament: Tournament)
    fun observeNewTournament(): SharedFlow<Tournament>
    suspend fun getTournamentById(id: String): LoadResult<Tournament, Throwable>
}