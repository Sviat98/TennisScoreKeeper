package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.routeStringToTournamentTab

class TournamentViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<TournamentState, TournamentUiEvent, TournamentAction>() {

    private val _state = MutableStateFlow(TournamentState.initial())
    override val state: StateFlow<TournamentState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentAction>
        get() = super.action

    init {
        val currentTabString = savedStateHandle.toRoute<TournamentRoute>().currentTab

        val currentTab = routeStringToTournamentTab(currentTabString)

        reduceState { oldState -> oldState.copy(currentTab = currentTab) }
    }

    fun onEvent(uiEvent: TournamentUiEvent) {
        // some feature-specific logic
    }

    private fun reduceState(reducer: (TournamentState) -> TournamentState) {
        _state.update(reducer)
    }

}