package com.bashkevich.tennisscorekeeper.model.tournament.remote

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TournamentApiUpcomingDto(
    @SerialName("api_id")
    val apiId: String,
    @SerialName("name")
    val name: String,
    @SerialName("start_date")
    val startDate: LocalDate,
    @SerialName("type")
    val type: String,
)
