package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantDownloadRequestDto
import kotlinx.coroutines.flow.Flow

interface ParticipantRepository {
    suspend fun getParticipantsForTournament(tournamentId: Int): LoadResult<List<TennisParticipant>, Throwable>
    suspend fun downloadParticipants(
        tournamentId: Int,
        downloadParticipantsBody: ParticipantDownloadRequestDto
    ): LoadResult<List<TennisParticipant>, Throwable>
    suspend fun fetchParticipantsForTournament(tournamentId: Int): LoadResult<Unit, Throwable>
    fun observeParticipantsForTournament(tournamentId: Int): Flow<List<TennisParticipant>>
    suspend fun deleteParticipantsForTournament(tournamentId: Int)
}