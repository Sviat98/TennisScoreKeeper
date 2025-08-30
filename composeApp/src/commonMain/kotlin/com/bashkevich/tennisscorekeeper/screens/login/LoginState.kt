package com.bashkevich.tennisscorekeeper.screens.login

import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class LoginUiEvent : UiEvent {
    data object Login: LoginUiEvent()
}

@Immutable
data class LoginState(
    val playerId: String
) : UiState {
    companion object {
        fun initial() = LoginState(
            playerId = ""
        )
    }
}

@Immutable
sealed class LoginAction : UiAction {

}