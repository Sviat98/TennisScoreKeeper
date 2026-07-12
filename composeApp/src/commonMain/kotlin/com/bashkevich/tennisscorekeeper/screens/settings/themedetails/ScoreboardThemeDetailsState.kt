package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.theme.ThemeColorField
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Immutable
data class ScoreboardThemeDetailsState(
    val loadingState: ThemeDetailsLoadingState = ThemeDetailsLoadingState.Loading,
    val action: ThemeDetailsAction? = null
) : UiState {
    companion object {
        fun initial() = ScoreboardThemeDetailsState(
            loadingState = ThemeDetailsLoadingState.Loading,
            action = null
        )
    }
}

@Immutable
sealed interface ThemeDetailsLoadingState : UiState {
    data object Loading : ThemeDetailsLoadingState
    data class Content(
        val oldTheme: ScoreboardTheme,
        val editedTheme: ScoreboardTheme,
        val isSaving: Boolean = false,
        val isRefreshing: Boolean = false,
    ) : ThemeDetailsLoadingState

    data class Error(val message: String) : ThemeDetailsLoadingState
}

@Immutable
sealed class ThemeDetailsUiEvent : UiEvent {
    data object Refresh : ThemeDetailsUiEvent()
    data object Retry : ThemeDetailsUiEvent()
    data class UpdateColor(val field: ThemeColorField, val color: Color) : ThemeDetailsUiEvent()
    data object SaveTheme : ThemeDetailsUiEvent()
}

@Immutable
sealed class ThemeDetailsAction : UiAction {
    data class ShowError(val message: String) : ThemeDetailsAction()
    data object ShowUnauthorizedActionError : ThemeDetailsAction()
    data object ThemeSaved : ThemeDetailsAction()
}
