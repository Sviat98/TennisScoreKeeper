package com.bashkevich.tennisscorekeeper.model.match.remote.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMatchBody(
    @SerialName("first_participant_display_name")
    val firstParticipantDisplayName: String,
    @SerialName("second_participant_display_name")
    val secondParticipantDisplayName: String,
    @SerialName("theme_id")
    val themeId: String,
)
