package com.bashkevich.tennisscorekeeper.screens.settings.main

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<SettingsState, SettingsUiEvent, SettingsAction>() {

    override val state: StateFlow<SettingsState> = combine(
        authRepository.observeLoggedInPlayer(),
        _action
    ) { loggedInPlayer, action ->
        SettingsState(loggedInPlayer = loggedInPlayer, action = action)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsState.initial()
    )

    fun onEvent(uiEvent: SettingsUiEvent) {
        when (uiEvent) {
            SettingsUiEvent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
