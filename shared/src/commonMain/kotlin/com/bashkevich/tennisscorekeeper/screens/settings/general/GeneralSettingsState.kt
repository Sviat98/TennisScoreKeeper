package com.bashkevich.tennisscorekeeper.screens.settings.general

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
data class GeneralSettingsState(
    val appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
) : UiState {
    companion object {
        fun initial() = GeneralSettingsState()
    }
}

@Immutable
sealed class GeneralSettingsUiEvent : UiEvent {
    data class ChangeThemeMode(val mode: AppThemeMode) : GeneralSettingsUiEvent()
}

@Immutable
sealed class GeneralSettingsAction : UiAction
