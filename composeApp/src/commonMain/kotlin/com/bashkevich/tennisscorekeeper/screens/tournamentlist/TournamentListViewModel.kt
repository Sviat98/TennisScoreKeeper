package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TournamentListViewModel(
    private val tournamentRepository: TournamentRepository
) :
    BaseViewModel<TournamentListState, TournamentListUiEvent, TournamentListAction>() {

    private val _state = MutableStateFlow(TournamentListState.initial())
    override val state: StateFlow<TournamentListState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentListAction>
        get() = super.action

    init {
        viewModelScope.launch {
            tournamentRepository.getTournaments().doOnSuccess { tournaments ->
                onEvent(TournamentListUiEvent.ShowTournaments(tournaments))
            }
        }

        viewModelScope.launch {
            tournamentRepository.observeNewTournament()
                .distinctUntilChanged { old, new -> old === new }.collect{tournamentBody ->
                    val loadResult = tournamentRepository.addTournament(tournamentBody)

                    if (loadResult is LoadResult.Success){
                        val newTournament = loadResult.result
                        val tournaments = state.value.tournaments.toMutableList()

                        tournaments.add(newTournament)
                        onEvent(TournamentListUiEvent.ShowTournaments(tournaments = tournaments.toList()))
                    }
                }
        }

    }

    fun onEvent(uiEvent: TournamentListUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is TournamentListUiEvent.ShowTournaments -> {
                reduceState { oldState -> oldState.copy(tournaments = uiEvent.tournaments) }
            }
        }
    }

    private fun reduceState(reducer: (TournamentListState) -> TournamentListState) {
        _state.update(reducer)
    }

}