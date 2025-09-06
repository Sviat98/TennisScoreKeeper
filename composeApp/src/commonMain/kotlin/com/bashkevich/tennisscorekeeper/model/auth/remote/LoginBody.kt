package com.bashkevich.tennisscorekeeper.model.auth.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    @SerialName("login")
    val login: String,
    @SerialName("password")
    val password: String
)
