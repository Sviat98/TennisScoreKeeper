package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun getMatchesForTournament(tournamentId: String): LoadResult<List<ShortMatch>, Throwable>
    suspend fun closeSession()
    fun connectToMatchUpdates(matchId: String)
    fun observeMatchUpdates(): Flow<LoadResult<Match, Throwable>>
    suspend fun updateMatchScore(matchId: String, participantId: String, scoreType: ScoreType)
    suspend fun undoPoint(matchId: String)
    suspend fun redoPoint(matchId: String)
    suspend fun attachVideoLink(matchId: String, videoLink: String)
    suspend fun setFirstParticipantToServe(matchId: String,participantId: String)
    suspend fun setFirstPlayerInPairToServe(matchId: String,playerId: String)
    suspend fun setParticipantRetired(matchId: String, participantId: String)
    suspend fun setMatchStatus(matchId: String,status: MatchStatus)
    fun emitNewMatch(newMatch: ShortMatch)
    suspend fun addNewMatch(
        tournamentId: String,
        matchBody: MatchBody
    ): LoadResult<ShortMatch, Throwable>

    fun observeNewMatch(): Flow<ShortMatch>
}