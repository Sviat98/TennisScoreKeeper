package com.bashkevich.tennisscorekeeper.model.tournament.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddTournamentBody(
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: TournamentType,
    @SerialName("regular_set_id")
    val regularSetTemplateId: String,
    @SerialName("deciding_set_id")
    val decidingSetTemplateId: String,
    @SerialName("theme_id")
    val themeId: String,
)
