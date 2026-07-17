package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.match.local.MatchLocalDataSource
import com.bashkevich.tennisscorekeeper.model.match.local.toDomain
import com.bashkevich.tennisscorekeeper.model.match.local.toMatchDomain
import com.bashkevich.tennisscorekeeper.model.match.local.toMatchWithParticipantsEntity
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ChangeScoreBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatusBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.RetiredParticipantBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeInPairBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.VideoLinkBody
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentLocalDataSource
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class MatchRepositoryImpl(
    private val matchRemoteDataSource: MatchRemoteDataSource,
    private val matchLocalDataSource: MatchLocalDataSource,
    private val tournamentLocalDataSource: TournamentLocalDataSource
) : MatchRepository {

    override suspend fun addNewMatch(
        tournamentId: Int,
        matchBody: MatchBody
    ): LoadResult<Unit, Throwable> {
        return matchRemoteDataSource.addNewMatch(tournamentId = tournamentId.toString(), matchBody = matchBody)
            .doOnSuccess { shortMatchDto ->
                matchLocalDataSource.insertMatch(shortMatchDto.toMatchWithParticipantsEntity())
                tournamentLocalDataSource.incrementUncompletedMatches(tournamentId)
            }
            .mapSuccess {  }
    }

    override suspend fun fetchMatchesForTournament(tournamentId: Int): LoadResult<Unit, Throwable> {
        return matchRemoteDataSource.getMatchesByTournament(tournamentId.toString())
            .doOnSuccess { shortMatchDtos ->
                val entities = shortMatchDtos.map { it.toMatchWithParticipantsEntity() }
                matchLocalDataSource.replaceMatchesForTournament(tournamentId, entities)
            }
            .mapSuccess { }
    }

    override fun observeMatchesForTournament(tournamentId: Int): Flow<List<ShortMatch>> {
        return matchLocalDataSource.observeMatches(tournamentId).map { matches ->
            matches.map { it.toDomain() }
        }
    }

    override suspend fun getMatchesForTournament(tournamentId: Int): LoadResult<List<ShortMatch>, Throwable> {
        return matchRemoteDataSource.getMatchesByTournament(tournamentId.toString())
            .mapSuccess { shortMatches -> shortMatches.map { it.toDomain() } }
    }

    override fun closeSession() {
        matchRemoteDataSource.closeSession()
    }

    override fun observeMatchById(matchId: Int): Flow<Match?> {
        return matchLocalDataSource.observeMatchById(matchId).
            onEach { println("match From Db = $it") }
            .map { it?.toMatchDomain() }
    }

    override fun observeMatchUpdatesFromNetwork(matchId: Int): Flow<LoadResult<Match, Throwable>> =
        matchRemoteDataSource.observeMatchUpdates()
            .onStart { matchRemoteDataSource.connectToMatchUpdates(matchId.toString()) }
            .onEach { result ->
                println("observeMatchUpdatesFromNetwork = $result")
                result.doOnSuccess { matchDto ->
                    matchLocalDataSource.insertMatch(matchDto.toMatchWithParticipantsEntity())                }
            }
            .map { result -> result.mapSuccess { matchDto -> matchDto.toDomain() } }

    override fun observeConnectionState(): StateFlow<ConnectionState> =
        matchRemoteDataSource.observeConnectionState()

    override suspend fun updateMatchScore(matchId: Int, participantId: Int, scoreType: ScoreType): LoadResult<ResponseMessage, Throwable> {
        val changeScoreBody = ChangeScoreBody(playerId = participantId.toString(), scoreType = scoreType)
        return matchRemoteDataSource.updateMatchScore(matchId = matchId.toString(), changeScoreBody = changeScoreBody)
    }

    override suspend fun undoPoint(matchId: Int): LoadResult<ResponseMessage, Throwable> {
        return matchRemoteDataSource.undoPoint(matchId = matchId.toString())
    }

    override suspend fun redoPoint(matchId: Int): LoadResult<ResponseMessage, Throwable> {
        return matchRemoteDataSource.redoPoint(matchId = matchId.toString())
    }

    override suspend fun attachVideoLink(matchId: Int, videoLink: String): LoadResult<ResponseMessage, Throwable> {
        val videoLinkBody = VideoLinkBody(videoLink)
        return matchRemoteDataSource.attachVideoLink(matchId = matchId.toString(), videoLinkBody = videoLinkBody)
    }

    override suspend fun setFirstParticipantToServe(matchId: Int, participantId: Int): LoadResult<ResponseMessage, Throwable> {
        val serveBody = ServeBody(participantId.toString())
        return matchRemoteDataSource.setFirstParticipantToServe(matchId = matchId.toString(), serveBody = serveBody)
    }

    override suspend fun setFirstPlayerInPairToServe(matchId: Int, playerId: Int): LoadResult<ResponseMessage, Throwable> {
        val serveInPairBody = ServeInPairBody(playerId.toString())
        return matchRemoteDataSource.setFirstServeInPair(
            matchId = matchId.toString(),
            serveInPairBody = serveInPairBody
        )
    }

    override suspend fun setParticipantRetired(matchId: Int, participantId: Int): LoadResult<ResponseMessage, Throwable> {
        val retiredParticipantBody = RetiredParticipantBody(retiredParticipantId = participantId.toString())
        return matchRemoteDataSource.setParticipantRetired(matchId = matchId.toString(), retiredParticipantBody = retiredParticipantBody)
    }

    override suspend fun setMatchStatus(matchId: Int, status: MatchStatus): LoadResult<ResponseMessage, Throwable> {
        val matchStatusBody = MatchStatusBody(status)
        return matchRemoteDataSource.updateMatchStatus(
            matchId = matchId.toString(),
            matchStatusBody = matchStatusBody
        )
    }

    override suspend fun deleteMatchesForTournament(tournamentId: Int) {
        matchLocalDataSource.deleteMatchesForTournament(tournamentId)
    }

    override suspend fun deleteAllMatchesFromDb() {
        matchLocalDataSource.deleteAllMatches()
    }
}
