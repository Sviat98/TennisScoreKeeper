package com.bashkevich.tennisscorekeeper.screens.counterlist

import com.bashkevich.tennisscorekeeper.model.counter.COUNTERS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel

class CounterListViewModel :
    BaseViewModel<CounterListState, CounterListUiEvent, CounterListAction>() {

    private val _state = MutableStateFlow(CounterListState.initial())
    override val state: StateFlow<CounterListState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterListAction>
        get() = super.action

    init {
        onEvent(CounterListUiEvent.ShowCounters(counters = COUNTERS))
    }

    fun onEvent(uiEvent: CounterListUiEvent) {
        when(uiEvent){
            is CounterListUiEvent.ShowCounters -> {
                reduceState { oldState-> oldState.copy(counters = uiEvent.counters) }
            }
        }
    }

    private fun reduceState(reducer: (CounterListState) -> CounterListState) {
        _state.update(reducer)
    }

}