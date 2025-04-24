package com.bashkevich.tennisscorekeeper.screens.matchdetails

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
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsUiEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MatchDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) :
    BaseViewModel<MatchDetailsState, MatchDetailsUiEvent, MatchDetailsAction>() {

    private val _state = MutableStateFlow(MatchDetailsState.initial())
    override val state: StateFlow<MatchDetailsState>
        get() = _state.asStateFlow()

    val actions: Flow<MatchDetailsAction>
        get() = super.action

    init {
        val matchId = savedStateHandle.toRoute<MatchDetailsRoute>().id

        matchRepository.connectToMatchUpdates(matchId = matchId)

        viewModelScope.launch {
            matchRepository.observeMatchUpdates().distinctUntilChanged().collect {result->
                println(result)
                when(result){
                    is LoadResult.Success->{
                        onEvent(MatchDetailsUiEvent.ShowMatch(result.result))
                    }
                    is LoadResult.Error->{
                        println(result.result.message)
                    }
                }
            }
        }
    }

    fun onEvent(uiEvent: MatchDetailsUiEvent) {
        // some feature-specific logic
        when(uiEvent){
            is MatchDetailsUiEvent.ShowMatch->{
                reduceState { oldState-> oldState.copy(match = uiEvent.match) }
            }
        }
    }

    private fun reduceState(reducer: (MatchDetailsState) -> MatchDetailsState) {
        _state.update(reducer)
    }

}