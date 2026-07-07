package com.bashkevich.tennisscorekeeper.model.theme.domain

import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeEntity
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeColor
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeContent
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeDto
import kotlinx.serialization.json.Json

data class ScoreboardTheme(
    val id: Int,
    val name: String,
    val mainBackgroundColor: Color,
    val mainTextColor: Color,
    val serveColor: Color,
    val previousSetWinTextColor: Color,
    val previousSetLoseTextColor: Color,
    val currentSetBackgroundColor: Color,
    val currentSetTextColor: Color,
    val currentGameBackgroundColor: Color,
    val currentGameTextColor: Color,
) {
    companion object {
        val DEFAULT = ScoreboardTheme(
            id = 0,
            name = "",
            mainBackgroundColor = Color.Blue,
            mainTextColor = Color.White,
            serveColor = Color.White,
            previousSetWinTextColor = Color.White,
            previousSetLoseTextColor = Color.White,
            currentSetBackgroundColor = Color.White,
            currentSetTextColor = Color.White,
            currentGameBackgroundColor = Color.White,
            currentGameTextColor = Color.White,
        )
    }
}

fun String.convertColor() = "FF$this".toLong(16)

fun ThemeColor.toColor() = Color(color.removePrefix("#").convertColor()).copy(alpha = alpha)

fun ThemeDto.toDomain() = ScoreboardTheme(
    id = id.toInt(),
    name = name,
    mainBackgroundColor = content.mainBackgroundColor.toColor(),
    mainTextColor = content.mainTextColor.toColor(),
    serveColor = content.serveColor.toColor(),
    previousSetWinTextColor = content.previousSetWinTextColor.toColor(),
    previousSetLoseTextColor = content.previousSetLoseTextColor.toColor(),
    currentSetBackgroundColor = content.currentSetBackgroundColor.toColor(),
    currentSetTextColor = content.currentSetTextColor.toColor(),
    currentGameBackgroundColor = content.currentGameBackgroundColor.toColor(),
    currentGameTextColor = content.currentGameTextColor.toColor(),
)

fun ThemeEntity.toDomain(): ScoreboardTheme {
    val content = Json.decodeFromString<ThemeContent>(this.content)
    return ScoreboardTheme(
        id = id,
        name = name,
        mainBackgroundColor = content.mainBackgroundColor.toColor(),
        mainTextColor = content.mainTextColor.toColor(),
        serveColor = content.serveColor.toColor(),
        previousSetWinTextColor = content.previousSetWinTextColor.toColor(),
        previousSetLoseTextColor = content.previousSetLoseTextColor.toColor(),
        currentSetBackgroundColor = content.currentSetBackgroundColor.toColor(),
        currentSetTextColor = content.currentSetTextColor.toColor(),
        currentGameBackgroundColor = content.currentGameBackgroundColor.toColor(),
        currentGameTextColor = content.currentGameTextColor.toColor(),
    )
}
