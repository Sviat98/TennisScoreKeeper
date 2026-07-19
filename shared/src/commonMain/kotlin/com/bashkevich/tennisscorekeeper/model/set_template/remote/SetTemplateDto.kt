package com.bashkevich.tennisscorekeeper.model.set_template.remote

import com.bashkevich.tennisscorekeeper.model.set_template.domain.TiebreakMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SetTemplateDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("games_to_win")
    val gamesToWin: Int,
    @SerialName("has_deciding_point")
    val hasDecidingPoint: Boolean,
    @SerialName("tiebreak_mode")
    val tiebreakMode: TiebreakMode,
    @SerialName("tiebreak_points_to_win")
    val tiebreakPointsToWin: Int,
    @SerialName("is_regular")
    val isRegular: Boolean,
    @SerialName("is_deciding")
    val isDeciding: Boolean,
)
