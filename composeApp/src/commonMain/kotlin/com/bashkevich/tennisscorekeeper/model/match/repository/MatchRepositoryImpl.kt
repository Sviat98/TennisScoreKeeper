package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ChangeScoreBody
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatusBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.RetiredParticipantBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeInPairBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class MatchRepositoryImpl(
    private val matchRemoteDataSource: MatchRemoteDataSource
) : MatchRepository {

    private val _newMatch = MutableSharedFlow<MatchBody>(replay = 1)

    override fun emitNewMatch(matchBody: MatchBody) {
        _newMatch.tryEmit(matchBody)
    }

    override suspend fun addNewMatch(
        tournamentId: String,
        matchBody: MatchBody
    ): LoadResult<ShortMatch, Throwable> {
        return matchRemoteDataSource.addNewMatch(tournamentId = tournamentId, matchBody = matchBody)
            .mapSuccess { shortMatchDto -> shortMatchDto.toDomain() }
    }

    override fun observeNewMatch(): Flow<MatchBody> = _newMatch.asSharedFlow()

    override suspend fun getMatchesForTournament(tournamentId: String): LoadResult<List<ShortMatch>, Throwable> {
        return matchRemoteDataSource.getMatchesByTournament(tournamentId)
            .mapSuccess { shortMatches -> shortMatches.map { it.toDomain() } }
    }

    override suspend fun closeSession() {
        matchRemoteDataSource.closeSession()
    }

    override fun connectToMatchUpdates(matchId: String) {
        matchRemoteDataSource.connectToMatchUpdates(matchId)
    }

    override fun observeMatchUpdates(): Flow<LoadResult<Match, Throwable>> =
        matchRemoteDataSource.observeMatchUpdates()
            .map { result -> result.mapSuccess { matchDto -> matchDto.toDomain() } }

    override suspend fun updateMatchScore(matchId: String, participantId: String, scoreType: ScoreType) {
        val changeScoreBody = ChangeScoreBody(playerId = participantId, scoreType = scoreType)

        matchRemoteDataSource.updateMatchScore(matchId = matchId, changeScoreBody = changeScoreBody)
    }

    override suspend fun undoPoint(matchId: String) {
        matchRemoteDataSource.undoPoint(matchId = matchId)
    }

    override suspend fun redoPoint(matchId: String) {
        matchRemoteDataSource.redoPoint(matchId = matchId)
    }

    override suspend fun setFirstParticipantToServe(matchId: String, participantId: String) {
        val serveBody = ServeBody(participantId)

        matchRemoteDataSource.setFirstParticipantToServe(matchId = matchId, serveBody = serveBody)
    }

    override suspend fun setFirstPlayerInPairToServe(matchId: String, playerId: String) {
        val serveInPairBody = ServeInPairBody(playerId)

        matchRemoteDataSource.setFirstServeInPair(
            matchId = matchId,
            serveInPairBody = serveInPairBody
        )
    }

    override suspend fun setParticipantRetired(matchId: String, participantId: String) {
        val retiredParticipantBody = RetiredParticipantBody(retiredParticipantId = participantId)

        matchRemoteDataSource.setParticipantRetired(matchId = matchId, retiredParticipantBody = retiredParticipantBody)
    }

    override suspend fun setMatchStatus(matchId: String, status: MatchStatus) {
        val matchStatusBody = MatchStatusBody(status)

        matchRemoteDataSource.updateMatchStatus(
            matchId = matchId,
            matchStatusBody = matchStatusBody
        )    }


}