package com.bashkevich.tennisscorekeeper.model.tournament.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import kotlinx.coroutines.flow.Flow

class TournamentLocalDataSource(
    private val db: AppDatabase
) {
    private val dao: TournamentDao = db.tournamentDao()

    fun getTournaments(): Flow<List<TournamentEntity>> {
        return dao.getAllTournaments()
    }

    fun getTournamentById(id: Int): Flow<TournamentEntity?> {
        return dao.getTournamentById(id)
    }

    suspend fun insertTournament(tournament: TournamentEntity) {
        dao.insertTournament(tournament)
    }

    suspend fun replaceAllTournaments(tournaments: List<TournamentEntity>) {
        dao.replaceAllTournaments(tournaments)
    }

    suspend fun updateStatus(id: Int, status: String) {
        dao.updateStatus(id, status)
    }

    suspend fun updateTotalParticipants(id: Int, amount: Int) {
        dao.updateTotalParticipants(id, amount)
    }

    suspend fun incrementUncompletedMatches(id: Int) {
        dao.incrementUncompletedMatches(id)
    }
}
