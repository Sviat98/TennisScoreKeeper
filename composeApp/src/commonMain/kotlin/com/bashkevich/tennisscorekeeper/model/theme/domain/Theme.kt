package com.bashkevich.tennisscorekeeper.model.theme

import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeEntity
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeColor
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeContent
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeDto
import kotlinx.serialization.json.Json

data class ScoreboardTheme(
    val id: String,
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
) {
    companion object {
        val DEFAULT = ScoreboardTheme("0", "", Color.Blue, Color.White)
    }
}

fun String.convertColor() = "FF$this".toLong(16)

fun ThemeColor.toColor() = Color(color.removePrefix("#").convertColor()).copy(alpha = alpha)

fun ThemeDto.toDomain() = ScoreboardTheme(
    id = id,
    name = name,
    backgroundColor = content.backgroundColor.toColor(),
    textColor = content.textColor.toColor(),
)

fun ThemeEntity.toDomain(): ScoreboardTheme {
    val content = Json.decodeFromString<ThemeContent>(this.content)
    return ScoreboardTheme(
        id = id,
        name = name,
        backgroundColor = content.backgroundColor.toColor(),
        textColor = content.textColor.toColor(),
    )
}
