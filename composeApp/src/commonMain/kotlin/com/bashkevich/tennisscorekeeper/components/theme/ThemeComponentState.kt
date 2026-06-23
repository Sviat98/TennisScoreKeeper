package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Immutable
data class ThemeComponentState(
    val selectedTheme: SelectedThemeState,
    val themeOptionsState: ThemeOptionsState,
){
    @Immutable
    sealed interface SelectedThemeState {

        @Immutable
        data class Loading(val initialThemeId: String) : SelectedThemeState

        @Immutable
        data class Error(val initialThemeId: String) : SelectedThemeState

        @Immutable
        data class Idle(val theme: ScoreboardTheme?) : SelectedThemeState
    }

    sealed interface ThemeOptionsState {
        @Immutable
        data object Loading : ThemeOptionsState

        @Immutable
        data class Error(val message: String) : ThemeOptionsState

        @Immutable
        data class Idle(val options: List<ScoreboardTheme>) : ThemeOptionsState
    }
}
