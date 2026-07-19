package com.bashkevich.tennisscorekeeper.model.tournament.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TournamentStatusBody(
    @SerialName("status")
    val tournamentStatus: TournamentStatus
)
