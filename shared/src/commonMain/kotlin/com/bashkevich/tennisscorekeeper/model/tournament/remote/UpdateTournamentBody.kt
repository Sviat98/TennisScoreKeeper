package com.bashkevich.tennisscorekeeper.model.tournament.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Только редактируемые поля турнира: название, тема, шаблоны сетов, sets_to_win
@Serializable
data class UpdateTournamentBody(
    @SerialName("name")
    val name: String,
    @SerialName("regular_set_id")
    val regularSetTemplateId: String? = null,
    @SerialName("deciding_set_id")
    val decidingSetTemplateId: String,
    @SerialName("theme_id")
    val themeId: String,
    @SerialName("sets_to_win")
    val setsToWin: Int,
)
