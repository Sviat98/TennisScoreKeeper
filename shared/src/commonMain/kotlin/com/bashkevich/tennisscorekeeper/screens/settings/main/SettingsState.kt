package com.bashkevich.tennisscorekeeper.screens.settings.main

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.model.auth.domain.LoggedInUser

@Immutable
sealed class SettingsUiEvent : UiEvent {
    data object Logout : SettingsUiEvent()
}

@Immutable
data class SettingsState(
    val loggedInUser: LoggedInUser,
    val action: SettingsAction? = null
) : UiState {
    companion object {
        fun initial() = SettingsState(
            loggedInUser = LoggedInUser.empty()
        )
    }
}

@Immutable
sealed class SettingsAction : UiAction
