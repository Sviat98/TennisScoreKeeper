package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import kotlinx.coroutines.launch

class TournamentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository
) : BaseViewModel<TournamentState, TournamentUiEvent, TournamentAction>() {

    private val _state = MutableStateFlow(TournamentState.initial())
    override val state: StateFlow<TournamentState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentAction>
        get() = super.action

    init {
        showTournament()
    }


    fun onEvent(uiEvent: TournamentUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is TournamentUiEvent.ChangeTournamentStatus -> changeTournamentStatus(uiEvent.tournamentStatus)

            is TournamentUiEvent.SelectTab -> {
                reduceState { oldState -> oldState.copy(currentTab = uiEvent.tournamentTab) }
            }
        }
    }

    private fun showTournament() {
        viewModelScope.launch {
            val tournamentId = savedStateHandle.toRoute<AddMatchRoute>().tournamentId

            val tournamentResult = tournamentRepository.getTournamentById(tournamentId)

            if (tournamentResult is LoadResult.Success) {

                val tournament = tournamentResult.result

                reduceState { oldState ->
                    oldState.copy(
                        tournament = tournament,
                    )
                }
            }
        }
    }

    private fun changeTournamentStatus(tournamentStatus: TournamentStatus) {
        viewModelScope.launch {

            val tournament = state.value.tournament

            val tournamentChangeStatusResult =
                tournamentRepository.changeTournamentStatus(tournament.id, tournamentStatus)

            if (tournamentChangeStatusResult is LoadResult.Success) {

                reduceState { oldState ->
                    oldState.copy(
                        tournament = oldState.tournament.copy(status = tournamentStatus)
                    )
                }
            }
        }
    }

    private fun reduceState(reducer: (TournamentState) -> TournamentState) {
        _state.update(reducer)
    }

}