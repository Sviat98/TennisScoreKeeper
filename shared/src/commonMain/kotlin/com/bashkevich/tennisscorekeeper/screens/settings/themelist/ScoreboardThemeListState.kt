package com.bashkevich.tennisscorekeeper.screens.settings.themelist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Immutable
data class ScoreboardThemeListState(
    val loadingState: ScoreboardThemeListLoadingState = ScoreboardThemeListLoadingState.Loading,
    val action: ScoreboardThemeListAction? = null
) : UiState {
    companion object {
        fun initial() = ScoreboardThemeListState(
            loadingState = ScoreboardThemeListLoadingState.Loading,
            action = null
        )
    }
}

@Immutable
sealed interface ScoreboardThemeListLoadingState : UiState {
    data object Loading : ScoreboardThemeListLoadingState
    data class Content(
        val themes: List<ScoreboardTheme>,
        val isRefreshing: Boolean = false,
    ) : ScoreboardThemeListLoadingState

    data class Error(val message: String) : ScoreboardThemeListLoadingState
}

@Immutable
sealed class ScoreboardThemeListUiEvent : UiEvent {
    data object RefreshThemes : ScoreboardThemeListUiEvent()
    data object Retry : ScoreboardThemeListUiEvent()
}

@Immutable
sealed class ScoreboardThemeListAction : UiAction {
    data class ShowError(val message: String) : ScoreboardThemeListAction()
}
