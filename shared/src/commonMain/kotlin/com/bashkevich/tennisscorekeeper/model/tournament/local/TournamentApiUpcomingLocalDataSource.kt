package com.bashkevich.tennisscorekeeper.model.tournament.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import kotlinx.coroutines.flow.Flow

class TournamentApiUpcomingLocalDataSource(
    private val db: AppDatabase
) {
    private val dao: TournamentApiUpcomingDao = db.tournamentApiUpcomingDao()

    fun getUpcomingTournaments(): Flow<List<TournamentApiUpcomingEntity>> {
        return dao.getAllUpcomingTournaments()
    }

    suspend fun replaceAllUpcomingTournaments(tournaments: List<TournamentApiUpcomingEntity>) {
        dao.replaceAllUpcomingTournaments(tournaments)
    }
}
