package com.bashkevich.tennisscorekeeper.model.participant.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantDto
import kotlinx.coroutines.flow.Flow

class ParticipantLocalDataSource(
    db: AppDatabase
) {
    private val dao: ParticipantDao = db.participantDao()

    fun observeParticipants(tournamentId: String): Flow<List<ParticipantWithPlayersEntity>> {
        return dao.getParticipantsForTournament(tournamentId)
    }

    suspend fun deleteParticipantsForTournament(tournamentId: String) {
        dao.deleteParticipantsByTournament(tournamentId)
    }

    suspend fun replaceParticipantsForTournament(tournamentId: String, dtos: List<ParticipantDto>) {
        val entities = dtos.map { it.toEntity(tournamentId) }
        dao.replaceAllParticipantsForTournament(tournamentId, entities)
    }
}
