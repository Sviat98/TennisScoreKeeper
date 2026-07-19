package com.bashkevich.tennisscorekeeper.model.player.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PlayerInMatchDto {
    @SerialName("id")
    abstract val id: String

    @SerialName("surname")
    abstract val surname: String

    @SerialName("name")
    abstract val name: String
}

@Serializable
@SerialName("singles_player")
data class PlayerInSinglesMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("surname")
    override val surname: String,
    @SerialName("name")
    override val name: String,
) : PlayerInMatchDto()

@Serializable
@SerialName("doubles_player")
data class PlayerInDoublesMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("surname")
    override val surname: String,
    @SerialName("name")
    override val name: String,
    @SerialName("is_serving_now")
    val isServingNow: Boolean,
    @SerialName("is_serving_next")
    val isServingNext: Boolean,
) : PlayerInMatchDto()
