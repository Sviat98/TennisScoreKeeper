package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

enum class ThemeColorField {
    MAIN_BACKGROUND_COLOR,
    MAIN_TEXT_COLOR,
    SERVE_COLOR,
    PREVIOUS_SET_WIN_TEXT_COLOR,
    PREVIOUS_SET_LOSE_TEXT_COLOR,
    CURRENT_SET_BACKGROUND_COLOR,
    CURRENT_SET_TEXT_COLOR,
    CURRENT_GAME_BACKGROUND_COLOR,
    CURRENT_GAME_TEXT_COLOR;

    fun getColor(theme: ScoreboardTheme): Color = when (this) {
        MAIN_BACKGROUND_COLOR -> theme.mainBackgroundColor
        MAIN_TEXT_COLOR -> theme.mainTextColor
        SERVE_COLOR -> theme.serveColor
        PREVIOUS_SET_WIN_TEXT_COLOR -> theme.previousSetWinTextColor
        PREVIOUS_SET_LOSE_TEXT_COLOR -> theme.previousSetLoseTextColor
        CURRENT_SET_BACKGROUND_COLOR -> theme.currentSetBackgroundColor
        CURRENT_SET_TEXT_COLOR -> theme.currentSetTextColor
        CURRENT_GAME_BACKGROUND_COLOR -> theme.currentGameBackgroundColor
        CURRENT_GAME_TEXT_COLOR -> theme.currentGameTextColor
    }

    fun applyTo(theme: ScoreboardTheme, color: Color): ScoreboardTheme = when (this) {
        MAIN_BACKGROUND_COLOR -> theme.copy(mainBackgroundColor = color)
        MAIN_TEXT_COLOR -> theme.copy(mainTextColor = color)
        SERVE_COLOR -> theme.copy(serveColor = color)
        PREVIOUS_SET_WIN_TEXT_COLOR -> theme.copy(previousSetWinTextColor = color)
        PREVIOUS_SET_LOSE_TEXT_COLOR -> theme.copy(previousSetLoseTextColor = color)
        CURRENT_SET_BACKGROUND_COLOR -> theme.copy(currentSetBackgroundColor = color)
        CURRENT_SET_TEXT_COLOR -> theme.copy(currentSetTextColor = color)
        CURRENT_GAME_BACKGROUND_COLOR -> theme.copy(currentGameBackgroundColor = color)
        CURRENT_GAME_TEXT_COLOR -> theme.copy(currentGameTextColor = color)
    }
}
