package com.bashkevich.tennisscorekeeper.model.match.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchBody(
    @SerialName("first_participant")
    val firstParticipant: ParticipantInMatchBody,
    @SerialName("second_participant")
    val secondParticipant: ParticipantInMatchBody,
    @SerialName("sets_to_win")
    val setsToWin: Int,
    @SerialName("regular_set_id")
    val regularSet: String? = null,
    @SerialName("deciding_set_id")
    val decidingSet: String
)

val EMPTY_MATCH_BODY =
    MatchBody(
        firstParticipant = ParticipantInMatchBody(id = "0", displayName = ""),
        secondParticipant = ParticipantInMatchBody(id = "0", displayName = ""),
        setsToWin = 1,
        regularSet = "",
        decidingSet = ""
    )

@Serializable
data class ParticipantInMatchBody(
    @SerialName("id")
    val id: String,
    @SerialName("display_name")
    val displayName: String,
)