package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import kotlinx.coroutines.launch

class AddMatchViewModel(
    savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository
) : BaseViewModel<AddMatchState, AddMatchUiEvent, AddMatchAction>() {

    private val _state = MutableStateFlow(AddMatchState.initial())
    override val state: StateFlow<AddMatchState>
        get() = _state.asStateFlow()

    val actions: Flow<AddMatchAction>
        get() = super.action

    init {
        viewModelScope.launch {
            val tournamentId = savedStateHandle.toRoute<AddMatchRoute>().tournamentId

            val tournamentResult = tournamentRepository.getTournamentById(tournamentId)

            if(tournamentResult is LoadResult.Success){
                onEvent(AddMatchUiEvent.ShowTournament(tournamentResult.result))
            }
        }
    }

    fun onEvent(uiEvent: AddMatchUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is AddMatchUiEvent.ShowTournament -> {
                reduceState { oldState ->
                    oldState.copy(
                        isLoading = false,
                        tournament = uiEvent.tournament
                    )
                }
            }
        }
    }

    private fun reduceState(reducer: (AddMatchState) -> AddMatchState) {
        _state.update(reducer)
    }

}