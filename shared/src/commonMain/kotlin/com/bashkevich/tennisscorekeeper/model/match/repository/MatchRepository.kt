package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MatchRepository {
    suspend fun fetchMatchesForTournament(tournamentId: Int): LoadResult<Unit, Throwable>
    fun observeMatchesForTournament(tournamentId: Int): Flow<List<ShortMatch>>

    suspend fun getMatchesForTournament(tournamentId: Int): LoadResult<List<ShortMatch>, Throwable>
    fun closeSession()
    fun observeMatchById(matchId: Int): Flow<Match?>
    fun observeMatchUpdatesFromNetworkAndSaveToDb(matchId: Int): Flow<LoadResult<Unit, Throwable>>

    /**
     * Чистый network-поток обновлений матча БЕЗ кэширования в БД.
     * Для экранов, которым нужны только свежие данные из сети (например, Scoreboard-виджет
     * в WebView стриминговых приложений, где OPFS/Room недоступен). MatchDetails использует
     * кэширующий [observeMatchUpdatesFromNetworkAndSaveToDb].
     */
    fun observeMatchUpdatesFromNetwork(matchId: Int): Flow<LoadResult<Match, Throwable>>
    fun observeConnectionState(): StateFlow<ConnectionState>

    suspend fun updateMatchScore(matchId: Int, participantId: Int, scoreType: ScoreType): LoadResult<ResponseMessage, Throwable>
    suspend fun undoPoint(matchId: Int): LoadResult<ResponseMessage, Throwable>
    suspend fun redoPoint(matchId: Int): LoadResult<ResponseMessage, Throwable>
    suspend fun attachVideoLink(matchId: Int, videoLink: String): LoadResult<ResponseMessage, Throwable>
    suspend fun setFirstParticipantToServe(matchId: Int, participantId: Int): LoadResult<ResponseMessage, Throwable>
    suspend fun setFirstPlayerInPairToServe(matchId: Int, playerId: Int): LoadResult<ResponseMessage, Throwable>
    suspend fun setParticipantRetired(matchId: Int, participantId: Int): LoadResult<ResponseMessage, Throwable>
    suspend fun setMatchStatus(matchId: Int, status: MatchStatus): LoadResult<ResponseMessage, Throwable>

    suspend fun deleteMatchesForTournament(tournamentId: Int)
    suspend fun deleteAllMatchesFromDb()
    suspend fun addNewMatch(
        tournamentId: Int,
        matchBody: MatchBody
    ): LoadResult<Unit, Throwable>
}