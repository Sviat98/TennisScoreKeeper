package com.bashkevich.tennisscorekeeper.model.auth.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokensResponseDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String
)
