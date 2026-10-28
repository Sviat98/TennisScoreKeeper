package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantLocalDataSource
import com.bashkevich.tennisscorekeeper.model.participant.local.toDomain
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantUploadRequestDto
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParticipantRepositoryImpl(
    private val participantRemoteDataSource: ParticipantRemoteDataSource,
    private val participantLocalDataSource: ParticipantLocalDataSource,
    private val tournamentLocalDataSource: TournamentLocalDataSource
) : ParticipantRepository {

    override suspend fun getParticipantsForTournament(tournamentId: Int): LoadResult<List<TennisParticipant>, Throwable> {
        return participantRemoteDataSource.getParticipants(tournamentId.toString()).mapSuccess { participants -> participants.map { it.toDomain() } }
    }

    override suspend fun uploadParticipants(tournamentId: Int, uploadParticipantsBody: ParticipantUploadRequestDto): LoadResult<List<TennisParticipant>, Throwable> {
        return participantRemoteDataSource.uploadParticipants(tournamentId.toString(), uploadParticipantsBody)
            .doOnSuccess { dtos ->
                participantLocalDataSource.replaceParticipantsForTournament(tournamentId, dtos)
                tournamentLocalDataSource.updateTotalParticipants(tournamentId, dtos.size)
                tournamentLocalDataSource.updateSeededParticipants(tournamentId, uploadParticipantsBody.seedAmount)
            }
            .mapSuccess { participants -> participants.map { it.toDomain() } }
    }

    override suspend fun fetchParticipantsForTournament(tournamentId: Int): LoadResult<Unit, Throwable> {
        return participantRemoteDataSource.getParticipants(tournamentId.toString())
            .doOnSuccess { dtos ->
                participantLocalDataSource.replaceParticipantsForTournament(tournamentId, dtos)
            }
            .mapSuccess { }
    }

    override suspend fun deleteParticipantsForTournament(tournamentId: Int) {
        participantLocalDataSource.deleteParticipantsForTournament(tournamentId)
    }

    override fun observeParticipantsForTournament(tournamentId: Int): Flow<List<TennisParticipant>> {
        return participantLocalDataSource.observeParticipants(tournamentId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
