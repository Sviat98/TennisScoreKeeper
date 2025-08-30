package com.bashkevich.tennisscorekeeper.screens.profile

import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class ProfileUiEvent : UiEvent {
    data object Logout: ProfileUiEvent()
}

@Immutable
data class ProfileState(
    val playerId: String
) : UiState {
    companion object {
        fun initial() = ProfileState(
            playerId = ""
        )
    }
}

@Immutable
sealed class ProfileAction : UiAction {

}