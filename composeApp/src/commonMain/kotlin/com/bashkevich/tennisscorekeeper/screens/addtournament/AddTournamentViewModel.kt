package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.launch

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
        when (uiEvent) {
            is AddTournamentUiEvent.SelectTournamentType -> {
                reduceState { oldState -> oldState.copy(tournamentType = uiEvent.tournamentType) }
            }

            is AddTournamentUiEvent.AddTournament -> addTournament(
                tournamentName = uiEvent.tournamentName,
                tournamentType = uiEvent.tournamentType
            )
        }
    }

    private fun addTournament(tournamentName: String, tournamentType: TournamentType) {
        viewModelScope.launch {
            reduceState { oldState -> oldState.copy(tournamentAddingSubstate = TournamentAddingSubstate.Loading) }
            val addTournamentBody = AddTournamentBody(tournamentName, tournamentType)
            val addTournamentResult = tournamentRepository.addTournament(addTournamentBody)

            when (addTournamentResult) {
                is LoadResult.Success -> {
                    val newCounter = addTournamentResult.result

                    tournamentRepository.emitNewTournament(newCounter)

                    reduceState { oldState -> oldState.copy(tournamentAddingSubstate = TournamentAddingSubstate.Success) }
                }

                is LoadResult.Error -> {
                    val errorMessage = addTournamentResult.result.message ?: ""
                    reduceState { oldState ->
                        oldState.copy(
                            tournamentAddingSubstate = TournamentAddingSubstate.Error(
                                message = errorMessage
                            )
                        )
                    }
                }
            }
        }
    }


    private fun reduceState(reducer: (AddTournamentState) -> AddTournamentState) {
        _state.update(reducer)
    }

}