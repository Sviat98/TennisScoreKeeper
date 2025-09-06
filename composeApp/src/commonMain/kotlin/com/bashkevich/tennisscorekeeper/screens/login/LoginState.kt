package com.bashkevich.tennisscorekeeper.screens.login

import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class LoginUiEvent : UiEvent {
    data object Login: LoginUiEvent()
    data class ChangeLogin(val login: String) : LoginUiEvent()
    data class ChangePassword(val password: String) : LoginUiEvent()
}

@Immutable
data class LoginState(
    val login: String,
    val password: String
) : UiState {
    companion object {
        fun initial() = LoginState(
            login = "",
            password = ""
        )
    }
}

@Immutable
sealed class LoginAction : UiAction {

}