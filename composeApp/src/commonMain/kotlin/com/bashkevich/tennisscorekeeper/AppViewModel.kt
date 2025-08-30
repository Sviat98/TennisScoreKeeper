package com.bashkevich.tennisscorekeeper

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.AuthRepository
import com.bashkevich.tennisscorekeeper.mvi.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AppViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(AppState.initial())

    val state: StateFlow<AppState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observePlayerId().distinctUntilChanged().collect{playerId->
                val isAuthorized = playerId.isNotEmpty()
                _state.value = _state.value.copy(isAuthorized = isAuthorized)
                println("AppViewModel isAuthorized = $isAuthorized")
            }
        }
    }
}

@Immutable
data class AppState(
    val isAuthorized: Boolean
): UiState {
    companion object{
        fun initial() = AppState(
            isAuthorized = false
        )
    }
}