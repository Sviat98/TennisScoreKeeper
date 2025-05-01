package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.Match
import com.bashkevich.tennisscorekeeper.model.match.SimpleMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.ScoreType
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun getMatches(): LoadResult<List<SimpleMatch>, Throwable>
    suspend fun closeSession()
    fun connectToMatchUpdates(matchId: String)
    fun observeMatchUpdates(): Flow<LoadResult<Match, Throwable>>
    suspend fun updateMatchScore(matchId: String,playerId: String, scoreType: ScoreType)
    suspend fun undoPoint(matchId: String)
    suspend fun redoPoint(matchId: String)

}