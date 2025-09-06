package com.bashkevich.tennisscorekeeper.screens.login

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginBody
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginState, LoginUiEvent, LoginAction>() {

    private val _state = MutableStateFlow(LoginState.initial())
    override val state: StateFlow<LoginState>
        get() = _state.asStateFlow()

    val actions: Flow<LoginAction>
        get() = super.action

    fun onEvent(uiEvent: LoginUiEvent) {
        when (uiEvent) {
            is LoginUiEvent.ChangeLogin -> changeLogin(uiEvent.login)
            is LoginUiEvent.ChangePassword -> changePassword(uiEvent.password)
            LoginUiEvent.Login -> login()
        }
        // some feature-specific logic
    }

    private fun changeLogin(login: String) {
        reduceState { oldState ->
            oldState.copy(login = login)
        }
    }

    private fun changePassword(password: String) {
        reduceState { oldState ->
            oldState.copy(password = password)
        }
    }

    private fun login() {
        viewModelScope.launch {
            val login = state.value.login
            val password = state.value.password

            val loginBody = LoginBody(login = login, password = password)
            authRepository.login(loginBody)
        }
    }

    private fun reduceState(reducer: (LoginState) -> LoginState) {
        _state.update(reducer)
    }

}