package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class TournamentListViewModel(
    private val refreshTournamentList: RefreshTournamentListScreenUseCase,
    private val tournamentRepository: TournamentRepository,
) : BaseViewModel<TournamentListState, TournamentListUiEvent, TournamentListAction>() {

    private val _isRefreshing = MutableStateFlow(false)

    private val _networkAndTournaments = combine(
        refreshTournamentList.fetchTournamentsFlow(),
        tournamentRepository.observeTournaments()
    ) { networkState, tournaments ->
        networkState to tournaments
    }
        .onEach { (networkState, tournaments) ->
            println("networkState = $networkState")
            if (networkState is LoadResult.Error && tournaments.isNotEmpty()) {
                sendAction(TournamentListAction.ShowRefreshError(
                    networkState.result.message ?: "Error"
                ))
            }
            if (networkState != null) {
                _isRefreshing.value = false
            }
        }

    override val state: StateFlow<TournamentListState> = combine(
        _networkAndTournaments,
        _isRefreshing,
        _action
    ) { (networkState, tournaments), isRefreshing, action ->
        val loadingState = when {
            !isRefreshing && networkState == null && tournaments.isEmpty() ->
                TournamentListLoadingState.Loading
            networkState is LoadResult.Error && tournaments.isEmpty() ->
                TournamentListLoadingState.Error(networkState.result.message ?: "Error")
            else ->
                TournamentListLoadingState.Content(tournaments, isRefreshing)
        }
        TournamentListState(loadingState = loadingState, action = action)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TournamentListState.initial()
    )

    fun refresh() {
        _isRefreshing.value = true
        refreshTournamentList.refreshTournaments()
        // _isRefreshing.value = false ставится после того, как вернется результат
    }

    fun retry() {
        refreshTournamentList.refreshTournaments()
    }

    fun onEvent(uiEvent: TournamentListUiEvent) {
        when (uiEvent) {
            is TournamentListUiEvent.RefreshTournaments -> refresh()
            is TournamentListUiEvent.Retry -> retry()
        }
    }
}
