package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MatchListViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) : BaseViewModel<MatchListState, MatchListUiEvent, MatchListAction>() {

    private val _state = MutableStateFlow(MatchListState.initial())
    override val state: StateFlow<MatchListState>
        get() = _state.asStateFlow()

    val actions: Flow<MatchListAction>
        get() = super.action

    init {

        val tournamentId = savedStateHandle.toRoute<TournamentRoute>().tournamentId

        viewModelScope.launch {
            onEvent(MatchListUiEvent.SetTournamentId(tournamentId))

            val loadResult = matchRepository.getMatchesForTournament(tournamentId)

            println(loadResult)

            if (loadResult is LoadResult.Success) {
                onEvent(MatchListUiEvent.ShowMatches(matches = loadResult.result))
            }
        }

        viewModelScope.launch {
            matchRepository.observeNewMatch().distinctUntilChanged { old, new ->
                old === new
            }.collect { matchBody ->

                val loadResult = matchRepository.addNewMatch(tournamentId,matchBody)

                if (loadResult is LoadResult.Success) {
                    val newMatch = loadResult.result
                    val matches = state.value.matches.toMutableList()

                    matches.add(newMatch)
                    onEvent(MatchListUiEvent.ShowMatches(matches = matches))
                }
            }
        }
    }

    fun onEvent(uiEvent: MatchListUiEvent) {
        when (uiEvent) {
            is MatchListUiEvent.SetTournamentId -> {
                reduceState { oldState -> oldState.copy(tournamentId = uiEvent.tournamentId) }
            }

            is MatchListUiEvent.ShowMatches -> {
                reduceState { oldState -> oldState.copy(matches = uiEvent.matches) }
            }
        }
    }

    private fun reduceState(reducer: (MatchListState) -> MatchListState) {
        _state.update(reducer)
    }

}