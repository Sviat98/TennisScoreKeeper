package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TournamentListViewModel(
    private val tournamentRepository: TournamentRepository,
    private val authRepository: AuthRepository,
) :
    BaseViewModel<TournamentListState, TournamentListUiEvent, TournamentListAction>() {

    private val _state = MutableStateFlow(TournamentListState.initial())
    override val state: StateFlow<TournamentListState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentListAction>
        get() = super.action

    init {
        showTournaments()

        viewModelScope.launch {
            tournamentRepository.observeTournaments().collect { tournaments ->
                reduceState { oldState ->
                    when (oldState.loadingState) {
                        TournamentListContentState.Loading,
                        TournamentListContentState.Refreshing ->
                            oldState.copy(loadingState = TournamentListContentState.Idle, tournaments = tournaments)

                        else -> oldState.copy(tournaments = tournaments)
                    }
                }
            }
        }
    }

    private fun showTournaments() {
        viewModelScope.launch {
            reduceState { it.copy(loadingState = TournamentListContentState.Loading) }

            val fetchTournamentsAsync = async { tournamentRepository.fetchTournaments() }
            val checkRefreshTokenAsync = async { authRepository.checkRefreshTokenStatus() }

            val tournamentsResult = fetchTournamentsAsync.await()
            checkRefreshTokenAsync.await() // fire-and-forget, errors ignored
            println("fetchTournaments result 111= $tournamentsResult")
            if (tournamentsResult is LoadResult.Error) {
                val message = tournamentsResult.result.message ?: "Unknown error"
                reduceState { it.copy(loadingState = TournamentListContentState.InitialError(message)) }
            }
        }
    }

    private fun refreshTournaments() {
        viewModelScope.launch {
            if (state.value.loadingState != TournamentListContentState.Idle) return@launch

            reduceState { it.copy(loadingState = TournamentListContentState.Refreshing) }

            val result = tournamentRepository.fetchTournaments()
            println("fetchTournaments result 222= $result")
            if (result is LoadResult.Error) {
                val message = result.result.message ?: "Unknown error"
                reduceState { it.copy(loadingState = TournamentListContentState.Idle) }
                sendAction(TournamentListAction.ShowRefreshError(message))
            }
        }
    }

    fun onEvent(uiEvent: TournamentListUiEvent) {
        when (uiEvent) {
            is TournamentListUiEvent.LoadTournaments -> showTournaments()
            is TournamentListUiEvent.RefreshTournaments -> refreshTournaments()
        }
    }

    private fun reduceState(reducer: (TournamentListState) -> TournamentListState) {
        _state.update(reducer)
    }

}
