package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.match.local.MatchLocalDataSource
import com.bashkevich.tennisscorekeeper.model.match.local.MatchWithParticipantsEntity
import com.bashkevich.tennisscorekeeper.model.match.local.toDomain
import com.bashkevich.tennisscorekeeper.model.match.local.toMatchDomain
import com.bashkevich.tennisscorekeeper.model.match.local.toMatchGameEntity
import com.bashkevich.tennisscorekeeper.model.match.local.toMatchSetEntities
import com.bashkevich.tennisscorekeeper.model.match.local.toParticipantInMatchEntities
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class MatchRepositoryImpl(
    private val matchRemoteDataSource: MatchRemoteDataSource,
    private val matchLocalDataSource: MatchLocalDataSource,
    private val tournamentLocalDataSource: TournamentLocalDataSource
) : MatchRepository {

    override suspend fun addNewMatch(
        tournamentId: String,
        matchBody: MatchBody
    ): LoadResult<Unit, Throwable> {
        return matchRemoteDataSource.addNewMatch(tournamentId = tournamentId, matchBody = matchBody)
            .doOnSuccess { shortMatchDto ->
                matchLocalDataSource.insertMatch(tournamentId, shortMatchDto)
                tournamentLocalDataSource.incrementUncompletedMatches(tournamentId)
            }
            .mapSuccess {  }
    }

    override suspend fun fetchMatchesForTournament(tournamentId: String): LoadResult<Unit, Throwable> {
        return matchRemoteDataSource.getMatchesByTournament(tournamentId)
            .doOnSuccess { shortMatchDtos ->
                matchLocalDataSource.replaceMatchesForTournament(tournamentId, shortMatchDtos)
            }
            .mapSuccess { }
    }

    override fun observeMatchesForTournament(tournamentId: String): Flow<List<ShortMatch>> {
        return matchLocalDataSource.observeMatches(tournamentId).map { matches ->
            matches.map { it.toDomain() }
        }
    }

    override suspend fun getMatchesForTournament(tournamentId: String): LoadResult<List<ShortMatch>, Throwable> {
        return matchRemoteDataSource.getMatchesByTournament(tournamentId)
            .mapSuccess { shortMatches -> shortMatches.map { it.toDomain() } }
    }

    override suspend fun closeSession() {
        matchRemoteDataSource.closeSession()
    }

    override fun observeMatchById(matchId: String): Flow<Match?> {
        return matchLocalDataSource.observeMatchById(matchId).
            onEach { println("match From Db = $it") }
            .map { it?.toMatchDomain() }
    }

    override fun observeMatchUpdatesFromNetwork(matchId: String): Flow<LoadResult<Match, Throwable>> =
        matchRemoteDataSource.observeMatchUpdates()
            .onStart { matchRemoteDataSource.connectToMatchUpdates(matchId) }
            .onEach { result ->
                println("observeMatchUpdatesFromNetwork = $result")
                result.doOnSuccess {
                    saveMatchDetailsToDb(it)
                }
            }
            .map { result -> result.mapSuccess { matchDto -> matchDto.toDomain() } }

    private suspend fun saveMatchDetailsToDb(matchDto: MatchDto) {
        val existingMatch = matchLocalDataSource.getMatchById(matchDto.id) ?: return

        val matchEntity = existingMatch.copy(
            status = matchDto.status.name,
            videoLink = matchDto.videoLink,
            pointShift = matchDto.pointShift,
            currentSetMode = matchDto.currentSetMode?.name
        )

        matchLocalDataSource.updateMatchDetails(
            match = matchEntity,
            sets = matchDto.toMatchSetEntities(),
            game = matchDto.toMatchGameEntity(),
            participants = matchDto.toParticipantInMatchEntities()
        )
    }

    override suspend fun updateMatchScore(matchId: String, participantId: String, scoreType: ScoreType): LoadResult<ResponseMessage, Throwable> {
        val changeScoreBody = ChangeScoreBody(playerId = participantId, scoreType = scoreType)
        return matchRemoteDataSource.updateMatchScore(matchId = matchId, changeScoreBody = changeScoreBody)
    }

    override suspend fun undoPoint(matchId: String): LoadResult<ResponseMessage, Throwable> {
        return matchRemoteDataSource.undoPoint(matchId = matchId)
    }

    override suspend fun redoPoint(matchId: String): LoadResult<ResponseMessage, Throwable> {
        return matchRemoteDataSource.redoPoint(matchId = matchId)
    }

    override suspend fun attachVideoLink(matchId: String, videoLink: String): LoadResult<ResponseMessage, Throwable> {
        val videoLinkBody = VideoLinkBody(videoLink)
        return matchRemoteDataSource.attachVideoLink(matchId = matchId, videoLinkBody = videoLinkBody)
    }

    override suspend fun setFirstParticipantToServe(matchId: String, participantId: String): LoadResult<ResponseMessage, Throwable> {
        val serveBody = ServeBody(participantId)
        return matchRemoteDataSource.setFirstParticipantToServe(matchId = matchId, serveBody = serveBody)
    }

    override suspend fun setFirstPlayerInPairToServe(matchId: String, playerId: String): LoadResult<ResponseMessage, Throwable> {
        val serveInPairBody = ServeInPairBody(playerId)
        return matchRemoteDataSource.setFirstServeInPair(
            matchId = matchId,
            serveInPairBody = serveInPairBody
        )
    }

    override suspend fun setParticipantRetired(matchId: String, participantId: String): LoadResult<ResponseMessage, Throwable> {
        val retiredParticipantBody = RetiredParticipantBody(retiredParticipantId = participantId)
        return matchRemoteDataSource.setParticipantRetired(matchId = matchId, retiredParticipantBody = retiredParticipantBody)
    }

    override suspend fun setMatchStatus(matchId: String, status: MatchStatus): LoadResult<ResponseMessage, Throwable> {
        val matchStatusBody = MatchStatusBody(status)
        return matchRemoteDataSource.updateMatchStatus(
            matchId = matchId,
            matchStatusBody = matchStatusBody
        )
    }

    override suspend fun deleteMatchesForTournament(tournamentId: String) {
        matchLocalDataSource.deleteMatchesForTournament(tournamentId)
    }

    override suspend fun deleteAllMatchesFromDb() {
        matchLocalDataSource.deleteAllMatches()
    }
}
