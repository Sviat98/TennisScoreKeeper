package com.bashkevich.tennisscorekeeper.model.tournament.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TournamentDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: TournamentType,
    @SerialName("status")
    val status: TournamentStatus,
)

enum class TournamentType {
    SINGLES, DOUBLES
}

fun TournamentType.mapToDisplayedString() = when (this) {
    TournamentType.SINGLES -> "Singles"
    TournamentType.DOUBLES -> "Doubles"
}

enum class TournamentStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}