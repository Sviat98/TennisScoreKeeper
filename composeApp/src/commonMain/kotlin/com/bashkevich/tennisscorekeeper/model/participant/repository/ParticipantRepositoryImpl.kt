package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantRemoteDataSource

class ParticipantRepositoryImpl(
    private val participantRemoteDataSource: ParticipantRemoteDataSource
) : ParticipantRepository {
    
    override suspend fun getParticipantsForTournament(tournamentId: String): LoadResult<List<TennisParticipant>, Throwable> {
        return participantRemoteDataSource.getParticipants(tournamentId).mapSuccess { participants -> participants.map { it.toDomain() } }
    }

    override suspend fun uploadParticipantsFile(tournamentId: String, participantsFile: ExcelFile): LoadResult<List<TennisParticipant>, Throwable> {
        return participantRemoteDataSource.uploadParticipantsFile(tournamentId,participantsFile).mapSuccess { participants -> participants.map { it.toDomain() } }
    }
}