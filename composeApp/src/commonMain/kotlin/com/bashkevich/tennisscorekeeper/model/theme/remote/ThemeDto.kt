package com.bashkevich.tennisscorekeeper.model.theme.remote

import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ThemeDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("content")
    val content: ThemeContent
)

@Serializable
data class ThemeContent(
    val backgroundColor: ThemeColor,
    val textColor: ThemeColor,
)

@Serializable
data class ThemeColor(
    val color: String,
    val alpha: Float = 1f,
)

