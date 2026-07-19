package com.bashkevich.tennisscorekeeper.model.theme.domain

sealed interface ScoreboardThemeState {
    data object Loading : ScoreboardThemeState
    data class Loaded(val theme: ScoreboardTheme) : ScoreboardThemeState
    data object Error : ScoreboardThemeState
}

fun ScoreboardThemeState.themeOrDefault(): ScoreboardTheme =
    (this as? ScoreboardThemeState.Loaded)?.theme ?: ScoreboardTheme.DEFAULT
