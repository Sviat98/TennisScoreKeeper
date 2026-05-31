package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.util.onetimeStateIn
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TournamentListViewModel(
    private val tournamentRepository: TournamentRepository,
    private val authRepository: AuthRepository,
) : BaseViewModel<TournamentListState, TournamentListUiEvent, TournamentListAction>() {

    // --- One-time initial fetch (lazy, OnetimeWhileSubscribed) ---

    private val initialLoadFlow: Flow<LoadResult<Unit, Throwable>> = flow {
        coroutineScope {
            val fetchTournamentsAsync = async { tournamentRepository.fetchTournaments() }
            val checkRefreshTokenAsync = async { authRepository.checkRefreshTokenStatus() }

            val result = fetchTournamentsAsync.await()
            checkRefreshTokenAsync.await()

            emit(result)
        }
    }

    private val initialLoadResult: StateFlow<LoadResult<Unit, Throwable>?> =
        initialLoadFlow.onetimeStateIn(viewModelScope, null)

    // --- Local DB observation (hot, survives navigation) ---

    private val tournaments: StateFlow<List<Tournament>> =
        tournamentRepository.observeTournaments()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    // --- Refresh ---

    private val _isRefreshing = MutableStateFlow(false)

    // --- Combined UI state ---

    override val state: StateFlow<TournamentListState> = combine(
        initialLoadResult,
        tournaments,
        _isRefreshing
    ) { loadResult, tournamentList, isRefreshing ->
        when {
            loadResult == null && tournamentList.isEmpty() -> TournamentListState.Loading
            loadResult is LoadResult.Error && tournamentList.isEmpty() ->
                TournamentListState.Error(loadResult.result.message ?: "Unknown error")
            else -> TournamentListState.Content(tournamentList, isRefreshing)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TournamentListState.Loading
    )

    val actions: Flow<TournamentListAction>
        get() = super.action

    // --- User actions ---

    fun refresh() {
        viewModelScope.launch {
            if (state.value !is TournamentListState.Content) return@launch

            _isRefreshing.value = true
            tournamentRepository.fetchTournaments()
                .doOnError { sendAction(TournamentListAction.ShowRefreshError("Error fetching tournaments list")) }
            _isRefreshing.value = false
        }
    }

    fun retry() {
        viewModelScope.launch {
            tournamentRepository.fetchTournaments()
                .doOnError { sendAction(TournamentListAction.ShowRefreshError("Error fetching tournaments list")) }
        }
    }

    fun onEvent(uiEvent: TournamentListUiEvent) {
        when (uiEvent) {
            is TournamentListUiEvent.RefreshTournaments -> refresh()
            is TournamentListUiEvent.Retry -> retry()
        }
    }
}
