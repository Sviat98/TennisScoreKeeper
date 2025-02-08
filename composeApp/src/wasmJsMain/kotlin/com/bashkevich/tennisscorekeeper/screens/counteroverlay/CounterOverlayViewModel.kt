package com.bashkevich.tennisscorekeeper.screens.counteroverlay

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.model.counter.COUNTERS
import com.bashkevich.tennisscorekeeper.model.counter.COUNTER_DEFAULT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.CounterOverlayRoute

class CounterOverlayViewModel(
    savedStateHandle: SavedStateHandle
) :
    BaseViewModel<CounterOverlayState, CounterOverlayUiEvent, CounterOverlayAction>() {

    private val _state = MutableStateFlow(CounterOverlayState.initial())
    override val state: StateFlow<CounterOverlayState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterOverlayAction>
        get() = super.action

    init {
        val counterId = savedStateHandle.toRoute<CounterOverlayRoute>().counterId

        val counter = COUNTERS.find { counter -> counter.id == counterId } ?: COUNTER_DEFAULT

        onEvent(CounterOverlayUiEvent.ShowCounter(counter))
    }

    fun onEvent(uiEvent: CounterOverlayUiEvent) {
        when(uiEvent){
            is CounterOverlayUiEvent.ShowCounter->{
                reduceState { oldState->
                    oldState.copy(counter = uiEvent.counter)
                }
            }
        }
    }

    private fun reduceState(reducer: (CounterOverlayState) -> CounterOverlayState) {
        _state.update(reducer)
    }

}