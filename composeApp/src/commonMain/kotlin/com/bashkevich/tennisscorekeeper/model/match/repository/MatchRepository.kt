package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun fetchMatchesForTournament(tournamentId: String): LoadResult<Unit, Throwable>
    fun observeMatchesForTournament(tournamentId: String): Flow<List<ShortMatch>>

    suspend fun getMatchesForTournament(tournamentId: String): LoadResult<List<ShortMatch>, Throwable>
    suspend fun closeSession()
    fun observeMatchById(matchId: String): Flow<Match?>
    fun observeMatchUpdatesFromNetwork(matchId: String): Flow<LoadResult<Match, Throwable>>

    suspend fun updateMatchScore(matchId: String, participantId: String, scoreType: ScoreType): LoadResult<ResponseMessage, Throwable>
    suspend fun undoPoint(matchId: String): LoadResult<ResponseMessage, Throwable>
    suspend fun redoPoint(matchId: String): LoadResult<ResponseMessage, Throwable>
    suspend fun attachVideoLink(matchId: String, videoLink: String): LoadResult<ResponseMessage, Throwable>
    suspend fun setFirstParticipantToServe(matchId: String, participantId: String): LoadResult<ResponseMessage, Throwable>
    suspend fun setFirstPlayerInPairToServe(matchId: String, playerId: String): LoadResult<ResponseMessage, Throwable>
    suspend fun setParticipantRetired(matchId: String, participantId: String): LoadResult<ResponseMessage, Throwable>
    suspend fun setMatchStatus(matchId: String, status: MatchStatus): LoadResult<ResponseMessage, Throwable>

    suspend fun deleteAllMatchesFromDb()
    suspend fun addNewMatch(
        tournamentId: String,
        matchBody: MatchBody
    ): LoadResult<Unit, Throwable>
}