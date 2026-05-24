package com.bashkevich.tennisscorekeeper.model.theme.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeContent
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeDto
import kotlinx.serialization.json.Json

@Entity(tableName = "scoreboard_theme")
data class ThemeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "content")
    val content: String
)

fun ThemeDto.toEntity() = ThemeEntity(
    id = id,
    name = name,
    content = Json.encodeToString(ThemeContent.serializer(), content)
)