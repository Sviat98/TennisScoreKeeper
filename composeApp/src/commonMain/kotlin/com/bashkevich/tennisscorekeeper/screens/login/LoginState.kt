package com.bashkevich.tennisscorekeeper.screens.login

import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class LoginUiEvent : UiEvent {
    data object Login : LoginUiEvent()
}

@Immutable
data class LoginState(
    val isLoggingIn: Boolean,
    val action: LoginAction? = null
) : UiState {
    companion object {
        fun initial() = LoginState(
            isLoggingIn = false
        )
    }
}

@Immutable
sealed class LoginAction : UiAction {
    data class ShowError(val message: String) : LoginAction()
}
