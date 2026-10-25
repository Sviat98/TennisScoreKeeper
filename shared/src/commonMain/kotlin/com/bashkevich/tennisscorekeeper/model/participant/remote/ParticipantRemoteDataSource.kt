package com.bashkevich.tennisscorekeeper.model.participant.remote

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ParticipantRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getParticipants(tournamentId: String): LoadResult<List<ParticipantDto>, Throwable> {

        return runOperationCatching {
            val participants = httpClient.get("/tournaments/$tournamentId/participants")
                .body<List<ParticipantDto>>()

            participants
        }
    }

    suspend fun uploadParticipants(
        tournamentId: String,
        uploadParticipantsBody: ParticipantUploadRequestDto
    ): LoadResult<List<ParticipantDto>, Throwable> {
        return runOperationCatching {
            httpClient.post("/tournaments/$tournamentId/participants/upload") {
                setBody(uploadParticipantsBody)
            }.body<List<ParticipantDto>>()
        }
    }
}
