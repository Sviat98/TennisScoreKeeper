package com.bashkevich.tennisscorekeeper.model.tournament.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentLocalDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.local.room.toEntity
import com.bashkevich.tennisscorekeeper.model.tournament.local.room.toDomain as entityToDomain
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatusBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class TournamentRepositoryImpl(
    private val tournamentRemoteDataSource: TournamentRemoteDataSource,
    private val tournamentLocalDataSource: TournamentLocalDataSource
) : TournamentRepository {
    private val _newTournament = MutableSharedFlow<Tournament>(replay = 1)

    override suspend fun getTournaments(): LoadResult<List<Tournament>, Throwable> {
        return tournamentRemoteDataSource.getTournaments().doOnSuccess { tournamentDtos ->
            val entities = tournamentDtos.map { it.toEntity() }
            tournamentLocalDataSource.replaceAllTournaments(entities)
        }.mapSuccess { tournamentDtos ->
            tournamentDtos.map { it.toDomain() }
        }
    }

    override suspend fun getTournamentById(id: String): LoadResult<Tournament, Throwable> {
        return tournamentRemoteDataSource.getTournamentById(id).mapSuccess { tournamentDto ->
            tournamentDto.toDomain()
        }
    }

    override suspend fun addTournament(addTournamentBody: AddTournamentBody): LoadResult<Tournament, Throwable> {
        return tournamentRemoteDataSource.addTournament(addTournamentBody)
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

    override fun emitNewTournament(newTournament: Tournament) {
        _newTournament.tryEmit(newTournament)
    }

    override fun observeNewTournament(): SharedFlow<Tournament> =
        _newTournament.asSharedFlow()

    override fun observeTournaments(): Flow<List<Tournament>> {
        return tournamentLocalDataSource.getTournaments().map { entities ->
            entities.map { it.entityToDomain() }
        }
    }

    override fun observeTournamentById(id: String): Flow<Tournament> {
        return tournamentLocalDataSource.getTournamentById(id).map { entity ->
            entity?.entityToDomain() ?: TOURNAMENT_DEFAULT
        }
    }
}
