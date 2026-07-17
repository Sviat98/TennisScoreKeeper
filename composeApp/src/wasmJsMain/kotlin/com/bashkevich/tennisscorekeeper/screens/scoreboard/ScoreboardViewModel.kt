package com.bashkevich.tennisscorekeeper.screens.scoreboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardRoute

class ScoreboardViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) : BaseViewModel<ScoreboardState, ScoreboardUiEvent, ScoreboardAction>() {

    private val matchId = savedStateHandle.toRoute<ScoreboardRoute>().matchId

    private val matchUpdates = matchRepository.observeMatchUpdatesFromNetwork(matchId)

    override val state: StateFlow<ScoreboardState> = combine(
        matchUpdates,
        matchRepository.observeConnectionState()
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
        matchRepository.closeSession()
    }
}
