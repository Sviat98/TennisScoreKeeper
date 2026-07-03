package com.bashkevich.tennisscorekeeper.model.participant.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantDto
import kotlinx.coroutines.flow.Flow

class ParticipantLocalDataSource(
    db: AppDatabase
) {
    private val dao: ParticipantDao = db.participantDao()

    fun observeParticipants(tournamentId: Int): Flow<List<ParticipantWithPlayersEntity>> {
        return dao.getParticipantsForTournament(tournamentId)
    }

    suspend fun deleteParticipantsForTournament(tournamentId: Int) {
        dao.deleteParticipantsByTournament(tournamentId)
    }

    suspend fun replaceParticipantsForTournament(tournamentId: Int, dtos: List<ParticipantDto>) {
        val entities = dtos.map { it.toEntity(tournamentId) }
        dao.replaceAllParticipantsForTournament(tournamentId, entities)
    }
}
