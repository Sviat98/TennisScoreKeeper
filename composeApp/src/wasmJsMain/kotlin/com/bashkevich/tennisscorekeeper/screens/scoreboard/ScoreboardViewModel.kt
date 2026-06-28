package com.bashkevich.tennisscorekeeper.screens.scoreboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardRoute
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState

class ScoreboardViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) : BaseViewModel<ScoreboardState, ScoreboardUiEvent, ScoreboardAction>() {

    private val matchId = savedStateHandle.toRoute<ScoreboardRoute>().matchId

    private val _connectionState = MutableStateFlow(ConnectionState.Loading)

    private val matchUpdates = matchRepository.observeMatchUpdatesFromNetwork(matchId)
        .onEach { result ->
            result
                .doOnSuccess { _connectionState.value = ConnectionState.Connected }
                .doOnError { _connectionState.value = ConnectionState.Disconnected }
        }

    override val state: StateFlow<ScoreboardState> = combine(
        matchUpdates,
        _connectionState
    ) { matchResult, connectionState ->
        val match = when (matchResult) {
            is LoadResult.Success -> matchResult.result
            is LoadResult.Error -> this@ScoreboardViewModel.state.value.match
        }
        ScoreboardState(match = match, connectionState = connectionState)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ScoreboardState.initial()
    )

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            matchRepository.closeSession()
        }
    }
}
