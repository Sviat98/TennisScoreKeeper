package com.bashkevich.tennisscorekeeper.screens.settings.general

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppLanguage
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
data class GeneralSettingsState(
    val appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
) : UiState {
    companion object {
        fun initial() = GeneralSettingsState()
    }
}

@Immutable
sealed class GeneralSettingsUiEvent : UiEvent {
    data class ChangeThemeMode(val mode: AppThemeMode) : GeneralSettingsUiEvent()
    data class ChangeLanguage(val language: AppLanguage) : GeneralSettingsUiEvent()
}

@Immutable
sealed class GeneralSettingsAction : UiAction
