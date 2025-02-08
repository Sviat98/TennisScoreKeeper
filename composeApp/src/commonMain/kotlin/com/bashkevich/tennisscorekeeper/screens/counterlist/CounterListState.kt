package com.bashkevich.tennisscorekeeper.screens.counterlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.counter.COUNTERS
import com.bashkevich.tennisscorekeeper.model.counter.Counter

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class CounterListUiEvent : UiEvent {
    data class ShowCounters(val counters: List<Counter>): CounterListUiEvent()
}

@Immutable
data class CounterListState(
    val counters: List<Counter>
) : UiState {
    companion object {
        fun initial() = CounterListState(
            counters = emptyList()
        )
    }
}

@Immutable
sealed class CounterListAction : UiAction {

}