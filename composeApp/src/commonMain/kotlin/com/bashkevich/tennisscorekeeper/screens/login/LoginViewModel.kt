package com.bashkevich.tennisscorekeeper.screens.login

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.AuthRepository
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
        when(uiEvent){
            LoginUiEvent.Login -> login()
        }
        // some feature-specific logic
    }

    private fun login(){
        viewModelScope.launch {
            authRepository.savePlayerId("1")
            authRepository.saveTokens(accessToken = "abc123", refreshToken = "def456")
        }
    }

    private fun reduceState(reducer: (LoginState) -> LoginState) {
        _state.update(reducer)
    }

}