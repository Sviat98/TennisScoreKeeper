package com.bashkevich.tennisscorekeeper.model.participant.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantDownloadRequestDto(
    @SerialName("seed_amount")
    val seedAmount: Int
)
