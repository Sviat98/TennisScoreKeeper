package com.bashkevich.tennisscorekeeper.model.theme

import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.theme.local.room.ThemeEntity
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeColor
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeContent
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeDto
import kotlinx.serialization.json.Json

data class CounterTheme(
    val id: String,
    val backgroundColor: Color,
    val textColor: Color,
) {
    companion object {
        val DEFAULT = CounterTheme("0", Color.Blue, Color.White)
    }
}

fun String.convertColor() = "FF$this".toLong(16)

fun ThemeColor.toColor() = Color(color.removePrefix("#").convertColor()).copy(alpha = alpha)

fun ThemeDto.toDomain() = CounterTheme(
    id = id,
    backgroundColor = content.backgroundColor.toColor(),
    textColor = content.textColor.toColor(),
)

fun ThemeEntity.toDomain(): CounterTheme {
    val content = Json.decodeFromString<ThemeContent>(this.content)
    return CounterTheme(
        id = id,
        backgroundColor = content.backgroundColor.toColor(),
        textColor = content.textColor.toColor(),
    )
}
