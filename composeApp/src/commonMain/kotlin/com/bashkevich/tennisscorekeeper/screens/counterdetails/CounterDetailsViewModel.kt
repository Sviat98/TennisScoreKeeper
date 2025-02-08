package com.bashkevich.tennisscorekeeper.screens.counterdetails

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
import com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute

class CounterDetailsViewModel(
    savedStateHandle: SavedStateHandle
) :
    BaseViewModel<CounterDetailsState, CounterDetailsUiEvent, CounterDetailsAction>() {

    private val _state = MutableStateFlow(CounterDetailsState.initial())
    override val state: StateFlow<CounterDetailsState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterDetailsAction>
        get() = super.action

    init {
        val id = savedStateHandle.toRoute<CounterDetailsRoute>().id

        val counter = COUNTERS.find { counter -> counter.id == id } ?: COUNTER_DEFAULT

        onEvent(CounterDetailsUiEvent.ShowCounter(counter))
    }

    fun onEvent(uiEvent: CounterDetailsUiEvent) {
        when(uiEvent){
            is CounterDetailsUiEvent.ShowCounter->{
                reduceState { oldState->
                    oldState.copy(counter = uiEvent.counter)
                }
            }
        }
    }

    private fun reduceState(reducer: (CounterDetailsState) -> CounterDetailsState) {
        _state.update(reducer)
    }

}