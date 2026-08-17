package com.bashkevich.tennisscorekeeper.screens.settings.generatetheme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.theme.ThemeColorField
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.model.file.domain.EMPTY_IMAGE_FILE
import com.bashkevich.tennisscorekeeper.model.file.domain.ImageFile
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Immutable
data class GenerateThemeState(
    val selectedImageName: String = "",
    val imageFile: ImageFile = EMPTY_IMAGE_FILE,
    // Тема ровно в том виде, в котором её вернул ИИ-агент (для Undo / старого значения)
    val originalTheme: ScoreboardTheme? = null,
    // Тема с правками пользователя
    val editedTheme: ScoreboardTheme? = null,
    val isGenerating: Boolean = false,
    val isSaving: Boolean = false,
    val action: GenerateThemeAction? = null
) : UiState {
    companion object {
        fun initial() = GenerateThemeState(
            selectedImageName = "",
            imageFile = EMPTY_IMAGE_FILE,
            originalTheme = null,
            editedTheme = null,
            isGenerating = false,
            isSaving = false,
            action = null
        )
    }
}

@Immutable
sealed class GenerateThemeUiEvent : UiEvent {
    data class SelectImage(val image: ImageFile) : GenerateThemeUiEvent()
    data object ClearImage : GenerateThemeUiEvent()
    data object Generate : GenerateThemeUiEvent()
    data class UpdateColor(val field: ThemeColorField, val color: Color) : GenerateThemeUiEvent()
    data object ResetTheme : GenerateThemeUiEvent()
    data object AddTheme : GenerateThemeUiEvent()
}

@Immutable
sealed class GenerateThemeAction : UiAction {
    data class ShowError(val message: String) : GenerateThemeAction()
    data object ShowUnauthorizedActionError : GenerateThemeAction()
    data object ThemeSaved : GenerateThemeAction()
}
