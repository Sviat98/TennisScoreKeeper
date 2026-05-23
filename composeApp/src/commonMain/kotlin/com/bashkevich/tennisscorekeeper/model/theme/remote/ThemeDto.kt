package com.bashkevich.tennisscorekeeper.model.theme.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

fun ThemeDto.toEntity() = com.bashkevich.tennisscorekeeper.model.theme.local.room.ThemeEntity(
    id = id,
    name = name,
    content = kotlinx.serialization.json.Json.encodeToString(ThemeContent.serializer(), content)
)
