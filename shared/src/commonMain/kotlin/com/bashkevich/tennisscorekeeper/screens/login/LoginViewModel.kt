package com.bashkevich.tennisscorekeeper.screens.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.NotFoundException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection
import com.bashkevich.tennisscorekeeper.model.auth.remote.LoginBody
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.launch
import tennisscorekeeper.shared.generated.resources.wrong_login_or_password

class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginState, LoginUiEvent, LoginAction>() {

    val loginTextFieldState = TextFieldState()
    val passwordTextFieldState = TextFieldState()
    private val _isLoading = MutableStateFlow(false)

    override val state: StateFlow<LoginState> = combine(
        _isLoading, _action
    ) { isLoading, action ->
        LoginState(isLoggingIn = isLoading, action = action)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        LoginState.initial()
    )

    fun onEvent(uiEvent: LoginUiEvent) {
        when (uiEvent) {
            LoginUiEvent.Login -> login()
        }
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is NetworkException ->
                sendAction(LoginAction.ShowError(getString(Res.string.check_internet_connection)))
            is NotFoundException ->
                sendAction(LoginAction.ShowError(message = getString(Res.string.wrong_login_or_password)))
            else ->
                sendAction(LoginAction.ShowError(e.message ?: "Error"))
        }
    }

    private fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            val loginBody = LoginBody(
                login = loginTextFieldState.text.toString(),
                password = passwordTextFieldState.text.toString()
            )
            authRepository.login(loginBody).doOnError { throwable ->
                handleError(throwable)
            }
            _isLoading.value = false
        }
    }
}
