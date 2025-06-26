package com.bashkevich.tennisscorekeeper.model.tournament.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatusBody
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TournamentRepositoryImpl(
    private val tournamentRemoteDataSource: TournamentRemoteDataSource
) : TournamentRepository {
    private val _newTournament = MutableSharedFlow<AddTournamentBody>(replay = 1)


    override suspend fun getTournaments(): LoadResult<List<Tournament>, Throwable> {
        return tournamentRemoteDataSource.getTournaments().mapSuccess { tournamentDtos ->
            val tournaments = tournamentDtos.map { it.toDomain() }
            tournaments
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

    override fun emitNewTournament(addTournamentBody: AddTournamentBody) {
        _newTournament.tryEmit(addTournamentBody)
    }

    override fun observeNewTournament(): SharedFlow<AddTournamentBody> =
        _newTournament.asSharedFlow()
}