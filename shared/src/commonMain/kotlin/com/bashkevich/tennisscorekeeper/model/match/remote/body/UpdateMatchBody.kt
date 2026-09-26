package com.bashkevich.tennisscorekeeper.model.match.remote.body

import com.bashkevich.tennisscorekeeper.model.match.remote.ParticipantInMatchBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMatchBody(
    @SerialName("first_participant")
    val firstParticipant: ParticipantInMatchBody,
    @SerialName("second_participant")
    val secondParticipant: ParticipantInMatchBody,
    @SerialName("theme_id")
    val themeId: String,
)
