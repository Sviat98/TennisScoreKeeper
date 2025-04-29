package com.bashkevich.tennisscorekeeper.screens.scoreboard

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
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardRoute
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ScoreboardViewModel(
    savedStateHandle: SavedStateHandle,
    matchRepository: MatchRepository
) : BaseViewModel<ScoreboardState, ScoreboardUiEvent, ScoreboardAction>() {

    private val _state = MutableStateFlow(ScoreboardState.initial())
    override val state: StateFlow<ScoreboardState>
        get() = _state.asStateFlow()

    val actions: Flow<ScoreboardAction>
        get() = super.action


    init {
        val matchId = savedStateHandle.toRoute<ScoreboardRoute>().matchId

        matchRepository.connectToMatchUpdates(matchId = matchId)

        viewModelScope.launch {
            matchRepository.observeMatchUpdates().distinctUntilChanged().collect {result->
                println(result)
                when(result){
                    is LoadResult.Success->{
                        onEvent(ScoreboardUiEvent.ShowMatch(result.result))
                    }
                    is LoadResult.Error->{
                        println(result.result.message)
                    }
                }
            }
        }
    }

    fun onEvent(uiEvent: ScoreboardUiEvent) {
        // some feature-specific logic
        when(uiEvent){
            is ScoreboardUiEvent.ShowMatch->{
                reduceState { oldState-> oldState.copy(match = uiEvent.match) }
            }
        }
    }

    private fun reduceState(reducer: (ScoreboardState) -> ScoreboardState) {
        _state.update(reducer)
    }

}