package com.bashkevich.tennisscorekeeper.screens.profile

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<ProfileState, ProfileUiEvent, ProfileAction>() {

    private val _state = MutableStateFlow(ProfileState.initial())
    override val state: StateFlow<ProfileState>
        get() = _state.asStateFlow()

    val actions: Flow<ProfileAction>
        get() = super.action

    init {
        viewModelScope.launch {
            authRepository.observePlayerId().distinctUntilChanged().collect {playerId->
                reduceState { oldState-> oldState.copy(playerId = playerId) }
            }
        }

    }

    fun onEvent(uiEvent: ProfileUiEvent) {
        // some feature-specific logic
        when(uiEvent) {
            ProfileUiEvent.Logout -> logout()
        }
    }

    private fun logout(){
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun reduceState(reducer: (ProfileState) -> ProfileState) {
        _state.update(reducer)
    }

}