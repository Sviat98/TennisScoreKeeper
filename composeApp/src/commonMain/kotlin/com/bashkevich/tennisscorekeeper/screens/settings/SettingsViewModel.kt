package com.bashkevich.tennisscorekeeper.screens.settings

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel

class SettingsViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<SettingsState, SettingsUiEvent, SettingsAction>() {

    override val state: StateFlow<SettingsState> = combine(
        authRepository.observePlayerId().distinctUntilChanged(),
        _action
    ) { playerId, action ->
        SettingsState(playerId = playerId, action = action)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsState.initial()
    )
}
