package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel

class TournamentViewModel: BaseViewModel<TournamentState, TournamentUiEvent, TournamentAction>() {

    private val _state = MutableStateFlow(TournamentState.initial())
    override val state: StateFlow<TournamentState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentAction>
        get() = super.action


    fun onEvent(uiEvent: TournamentUiEvent) {
        // some feature-specific logic
        when(uiEvent){
            is TournamentUiEvent.SelectTab -> {
                reduceState { oldState -> oldState.copy(currentTab = uiEvent.tournamentTab) }
            }
        }
    }

    private fun reduceState(reducer: (TournamentState) -> TournamentState) {
        _state.update(reducer)
    }

}