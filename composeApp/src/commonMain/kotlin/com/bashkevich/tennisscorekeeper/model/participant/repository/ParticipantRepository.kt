package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant

interface ParticipantRepository {
    suspend fun getParticipantsForTournament(tournamentId: String): LoadResult<List<TennisParticipant>, Throwable>
}