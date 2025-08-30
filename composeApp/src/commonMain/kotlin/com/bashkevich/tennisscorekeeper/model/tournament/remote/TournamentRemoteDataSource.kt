package com.bashkevich.tennisscorekeeper.model.tournament.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class TournamentRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getTournaments(): LoadResult<List<TournamentDto>, Throwable> {
        return runOperationCatching {
            val tournaments = httpClient.get("/tournaments").body<List<TournamentDto>>()
            tournaments
        }
    }

    suspend fun getTournamentById(id: String): LoadResult<TournamentDto, Throwable> {
        return runOperationCatching {
            val tournament = httpClient.get("/tournaments/$id").body<TournamentDto>()

            tournament
        }
    }

    suspend fun addTournament(tournamentBody: AddTournamentBody): LoadResult<TournamentDto, Throwable> {
        return runOperationCatching {
            val tournamentDto = httpClient.post("/tournaments") {
                setBody(tournamentBody)
            }.body<TournamentDto>()

            tournamentDto
        }
    }

    suspend fun changeTournamentStatus(tournamentId: String, tournamentStatusBody: TournamentStatusBody): LoadResult<ResponseMessage, Throwable>{
        return runOperationCatching {
            val tournamentStatusUpdateResult = httpClient.patch("/tournaments/$tournamentId/status") {
                setBody(tournamentStatusBody)
            }.body<ResponseMessage>()

            tournamentStatusUpdateResult
        }
    }
}