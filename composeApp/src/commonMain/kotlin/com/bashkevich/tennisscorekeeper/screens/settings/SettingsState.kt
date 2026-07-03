package com.bashkevich.tennisscorekeeper.screens.settings

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class SettingsUiEvent : UiEvent

@Immutable
data class SettingsState(
    val playerId: String,
    val action: SettingsAction? = null
) : UiState {
    companion object {
        fun initial() = SettingsState(
            playerId = ""
        )
    }
}

@Immutable
sealed class SettingsAction : UiAction
