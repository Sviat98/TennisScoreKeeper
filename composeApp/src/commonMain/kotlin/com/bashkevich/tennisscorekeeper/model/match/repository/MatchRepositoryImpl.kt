package com.bashkevich.tennisscorekeeper.model.match.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.SimpleMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.match.remote.ChangeScoreBody
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.match.remote.ScoreType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchRepositoryImpl(
    private val matchRemoteDataSource: MatchRemoteDataSource
) : MatchRepository{
    override suspend fun getMatches(): LoadResult<List<SimpleMatch>, Throwable> {
        return LoadResult.Success(listOf(
            SimpleMatch("1","Djokovic", "Auger Aliassime", "not started"),
            SimpleMatch("2","Djokovic/Nadal", "Murray/Federer", "not started"),
            )
        )
    }

    override suspend fun closeSession() {
        matchRemoteDataSource.closeSession()
    }

    override fun connectToMatchUpdates(matchId: String) {
        matchRemoteDataSource.connectToMatchUpdates(matchId)
    }

    override fun observeMatchUpdates(): Flow<LoadResult<Match, Throwable>> = matchRemoteDataSource.observeMatchUpdates()
        .map { result -> result.mapSuccess { matchDto -> matchDto.toDomain() } }

    override suspend fun updateMatchScore(matchId: String, playerId: String, scoreType: ScoreType) {
        val changeScoreBody = ChangeScoreBody(playerId = playerId, scoreType = scoreType)

        matchRemoteDataSource.updateMatchScore(matchId = matchId, changeScoreBody = changeScoreBody)
    }

    override suspend fun undoPoint(matchId: String) {
        matchRemoteDataSource.undoPoint(matchId = matchId)
    }

    override suspend fun redoPoint(matchId: String) {
        matchRemoteDataSource.redoPoint(matchId = matchId)
    }


}