package com.bashkevich.tennisscorekeeper.model.tournament.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface TournamentRepository {
    suspend fun fetchTournaments(): LoadResult<Unit, Throwable>
    suspend fun fetchTournamentById(id: String): LoadResult<Unit, Throwable>
    suspend fun addTournament(addTournamentBody: AddTournamentBody): LoadResult<Tournament, Throwable>
    suspend fun changeTournamentStatus(tournamentId: String, tournamentStatus: TournamentStatus): LoadResult<ResponseMessage, Throwable>
    fun emitNewTournament(newTournament: Tournament)
    fun observeNewTournament(): SharedFlow<Tournament>
    fun observeTournaments(): Flow<List<Tournament>>
    fun observeTournamentById(id: String): Flow<Tournament>
}
