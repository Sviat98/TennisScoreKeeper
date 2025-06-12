package com.bashkevich.tennisscorekeeper.model.participant.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ParticipantRemoteDataSource(
    private val httpClient: HttpClient,
) {
        suspend fun getParticipants(tournamentId: String): LoadResult<List<ParticipantDto>, Throwable> {

        return runOperationCatching {
            val participants = httpClient.get("/tournaments/$tournamentId/participants").body<List<ParticipantDto>>()

            participants
        }
    }
}