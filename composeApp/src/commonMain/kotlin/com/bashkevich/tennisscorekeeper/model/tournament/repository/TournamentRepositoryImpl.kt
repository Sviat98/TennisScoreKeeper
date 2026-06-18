package com.bashkevich.tennisscorekeeper.model.tournament.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapError
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentLocalDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.local.toEntity
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatusBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class TournamentRepositoryImpl(
    private val tournamentRemoteDataSource: TournamentRemoteDataSource,
    private val tournamentLocalDataSource: TournamentLocalDataSource
) : TournamentRepository {

    override suspend fun fetchTournaments(): LoadResult<Unit, Throwable>{
        return tournamentRemoteDataSource.getTournaments()
            .doOnSuccess {
                tournamentLocalDataSource.replaceAllTournaments(it.map { it.toEntity() })
            }
            .mapSuccess {  }
    }

    override suspend fun fetchTournamentById(id: String): LoadResult<Unit, Throwable> {
        return tournamentRemoteDataSource.getTournamentById(id).doOnSuccess { tournamentDto ->
            tournamentLocalDataSource.insertTournament(tournamentDto.toEntity())
        }.mapSuccess { }
    }

    override suspend fun addTournament(addTournamentBody: AddTournamentBody): LoadResult<Tournament, Throwable> {
        return tournamentRemoteDataSource.addTournament(addTournamentBody)
            .doOnSuccess { tournamentDto ->
                tournamentLocalDataSource.insertTournament(tournamentDto.toEntity())
            }
            .mapSuccess { tournamentDto ->
                tournamentDto.toDomain()
            }
    }

    override suspend fun changeTournamentStatus(
        tournamentId: String,
        tournamentStatus: TournamentStatus
    ): LoadResult<ResponseMessage, Throwable> {
        val tournamentStatusBody = TournamentStatusBody(tournamentStatus)
        return tournamentRemoteDataSource.changeTournamentStatus(
            tournamentId = tournamentId,
            tournamentStatusBody = tournamentStatusBody
        )
    }

    override suspend fun insertTournament(tournament: Tournament) {
        tournamentLocalDataSource.insertTournament(tournament.toEntity())
    }

    override fun observeTournaments(): Flow<List<Tournament>> {
        return tournamentLocalDataSource.getTournaments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTournamentById(id: String): Flow<Tournament> {
        return tournamentLocalDataSource.getTournamentById(id).map { entity ->
            entity?.toDomain() ?: TOURNAMENT_DEFAULT
        }
    }
}
