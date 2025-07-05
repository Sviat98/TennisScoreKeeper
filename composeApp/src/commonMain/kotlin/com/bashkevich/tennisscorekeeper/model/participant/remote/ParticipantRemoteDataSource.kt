package com.bashkevich.tennisscorekeeper.model.participant.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

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

    suspend fun uploadParticipantsFile(
        tournamentId: String,
        participantsFIle: ExcelFile
    ): LoadResult<List<ParticipantDto>, Throwable> {
        return runOperationCatching {
            println("Trying to send request")
            val response = httpClient.post("/tournaments/$tournamentId/participants/upload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("description", "Participants for a tournament $tournamentId")
                            append("excelFile", participantsFIle.content, Headers.build {
                                append(HttpHeaders.ContentType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${participantsFIle.name}\""
                                )
                            })
                        },
                    )
                )
            }

            val participants = response.body<List<ParticipantDto>>()

            println("participants result = $participants")

            participants
        }
    }
}