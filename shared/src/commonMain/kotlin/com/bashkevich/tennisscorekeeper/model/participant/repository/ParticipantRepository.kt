package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import kotlinx.coroutines.flow.Flow

interface ParticipantRepository {
    suspend fun getParticipantsForTournament(tournamentId: Int): LoadResult<List<TennisParticipant>, Throwable>
    suspend fun uploadParticipantsFile(
        tournamentId: Int,
        participantsFile: ExcelFile
    ): LoadResult<List<TennisParticipant>, Throwable>
    suspend fun fetchParticipantsForTournament(tournamentId: Int): LoadResult<Unit, Throwable>
    fun observeParticipantsForTournament(tournamentId: Int): Flow<List<TennisParticipant>>
    suspend fun deleteParticipantsForTournament(tournamentId: Int)
}