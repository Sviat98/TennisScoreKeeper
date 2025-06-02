package com.bashkevich.tennisscorekeeper.screens.addtournament

import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel

class AddTournamentViewModel(
    private val tournamentRepository: TournamentRepository
) :
    BaseViewModel<AddTournamentState, AddTournamentUiEvent, AddTournamentAction>() {

    private val _state = MutableStateFlow(AddTournamentState.initial())
    override val state: StateFlow<AddTournamentState>
        get() = _state.asStateFlow()

    val actions: Flow<AddTournamentAction>
        get() = super.action

    fun onEvent(uiEvent: AddTournamentUiEvent) {
        // some feature-specific logic
        when(uiEvent){
            is AddTournamentUiEvent.SelectTournamentType->{
                reduceState { oldState -> oldState.copy(tournamentType = uiEvent.tournamentType) }
            }
            is AddTournamentUiEvent.AddTournament->{
                val addTournamentBody = AddTournamentBody(uiEvent.tournamentName,uiEvent.tournamentType)

                tournamentRepository.emitNewTournament(addTournamentBody)
            }
        }
    }

    private fun reduceState(reducer: (AddTournamentState) -> AddTournamentState) {
        _state.update(reducer)
    }

}