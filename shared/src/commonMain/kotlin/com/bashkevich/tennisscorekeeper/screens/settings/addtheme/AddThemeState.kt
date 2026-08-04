package com.bashkevich.tennisscorekeeper.screens.settings.addtheme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.theme.ThemeColorField
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Immutable
data class AddThemeState(
    val editedTheme: ScoreboardTheme = ScoreboardTheme.DEFAULT,
    val isSaving: Boolean = false,
    val action: AddThemeAction? = null
) : UiState {
    companion object {
        fun initial() = AddThemeState(
            editedTheme = ScoreboardTheme.DEFAULT,
            isSaving = false,
            action = null
        )
    }
}

@Immutable
sealed class AddThemeUiEvent : UiEvent {
    data class UpdateColor(val field: ThemeColorField, val color: Color) : AddThemeUiEvent()
    data object AddTheme : AddThemeUiEvent()
}

@Immutable
sealed class AddThemeAction : UiAction {
    data class ShowError(val message: String) : AddThemeAction()
    data object ShowUnauthorizedActionError : AddThemeAction()
    data object ThemeSaved : AddThemeAction()
}
