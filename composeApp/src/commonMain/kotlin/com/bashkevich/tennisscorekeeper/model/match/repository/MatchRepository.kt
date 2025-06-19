package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.ScoreType
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun getMatchesForTournament(tournamentId: String): LoadResult<List<ShortMatch>, Throwable>
    suspend fun closeSession()
    fun connectToMatchUpdates(matchId: String)
    fun observeMatchUpdates(): Flow<LoadResult<Match, Throwable>>
    suspend fun updateMatchScore(matchId: String,playerId: String, scoreType: ScoreType)
    suspend fun undoPoint(matchId: String)
    suspend fun redoPoint(matchId: String)

    fun emitNewMatch(matchBody: MatchBody)
    suspend fun addNewMatch(
        tournamentId: String,
        matchBody: MatchBody
    ): LoadResult<ShortMatch, Throwable>

    fun observeNewMatch(): Flow<MatchBody>
}