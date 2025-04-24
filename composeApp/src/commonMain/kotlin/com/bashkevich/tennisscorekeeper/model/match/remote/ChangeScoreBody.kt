package com.bashkevich.tennisscorekeeper.model.match.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChangeScoreBody(
    @SerialName("player_id")
    val playerId: Int,
    @SerialName("score_type")
    val scoreType: ScoreType,
)

enum class ScoreType{
    POINT,GAME
}