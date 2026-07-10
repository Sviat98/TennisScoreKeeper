package com.bashkevich.tennisscorekeeper.model.theme.domain

import androidx.compose.runtime.staticCompositionLocalOf
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
            name = "Default",
            mainBackgroundColor = Color.Black,
            mainTextColor = Color.White,
            serveColor = Color.White,
            previousSetWinTextColor = Color.White,
            previousSetLoseTextColor = Color.White.copy(alpha = 0.7f),
            currentSetBackgroundColor = Color.Gray,
            currentSetTextColor = Color.Black,
            currentGameBackgroundColor = Color.White,
            currentGameTextColor = Color.Black,
        )

        val DEFAULT_1 = ScoreboardTheme(
            id = -1,
            name = "Default 111",
            mainBackgroundColor = Color(0xFF142c6c),
            mainTextColor = Color.White,
            serveColor = Color.Yellow,
            previousSetWinTextColor = Color.White,
            previousSetLoseTextColor = Color.White.copy(alpha = 0.5f),
            currentSetBackgroundColor = Color.Yellow,
            currentSetTextColor = Color.Black,
            currentGameBackgroundColor = Color.White,
            currentGameTextColor = Color.Black,
        )
    }
}

val LocalScoreboardTheme = staticCompositionLocalOf<ScoreboardTheme> {
    error("No ScoreboardTheme provided")
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
