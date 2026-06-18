package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantLocalDataSource
import com.bashkevich.tennisscorekeeper.model.participant.local.toDomain
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParticipantRepositoryImpl(
    private val participantRemoteDataSource: ParticipantRemoteDataSource,
    private val participantLocalDataSource: ParticipantLocalDataSource
) : ParticipantRepository {

    override suspend fun getParticipantsForTournament(tournamentId: String): LoadResult<List<TennisParticipant>, Throwable> {
        return participantRemoteDataSource.getParticipants(tournamentId).mapSuccess { participants -> participants.map { it.toDomain() } }
    }

    override suspend fun uploadParticipantsFile(tournamentId: String, participantsFile: ExcelFile): LoadResult<List<TennisParticipant>, Throwable> {
        return participantRemoteDataSource.uploadParticipantsFile(tournamentId, participantsFile)
            .doOnSuccess { dtos ->
                participantLocalDataSource.replaceParticipantsForTournament(tournamentId, dtos)
            }
            .mapSuccess { participants -> participants.map { it.toDomain() } }
    }

    override suspend fun fetchParticipantsForTournament(tournamentId: String): LoadResult<Unit, Throwable> {
        return participantRemoteDataSource.getParticipants(tournamentId)
            .doOnSuccess { dtos ->
                participantLocalDataSource.replaceParticipantsForTournament(tournamentId, dtos)
            }
            .mapSuccess { }
    }

    override fun observeParticipantsForTournament(tournamentId: String): Flow<List<TennisParticipant>> {
        return participantLocalDataSource.observeParticipants(tournamentId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
