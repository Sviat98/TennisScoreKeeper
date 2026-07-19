package com.bashkevich.tennisscorekeeper.model.auth.remote

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("player")
    val player: PlayerDto,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String
)
