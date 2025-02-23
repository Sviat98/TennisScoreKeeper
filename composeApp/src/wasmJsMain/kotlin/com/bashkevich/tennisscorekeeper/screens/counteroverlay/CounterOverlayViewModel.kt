package com.bashkevich.tennisscorekeeper.screens.counteroverlay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.counter.COUNTERS
import com.bashkevich.tennisscorekeeper.model.counter.COUNTER_DEFAULT
import com.bashkevich.tennisscorekeeper.model.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.CounterOverlayRoute
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsUiEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class CounterOverlayViewModel(
    savedStateHandle: SavedStateHandle,
    private val counterRepository: CounterRepository
) :
    BaseViewModel<CounterOverlayState, CounterOverlayUiEvent, CounterOverlayAction>() {

    private val _state = MutableStateFlow(CounterOverlayState.initial())
    override val state: StateFlow<CounterOverlayState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterOverlayAction>
        get() = super.action

    init {
        val counterId = savedStateHandle.toRoute<CounterOverlayRoute>().counterId

        counterRepository.connectToCounterUpdates(counterId = counterId)

        viewModelScope.launch {
            counterRepository.observeCounterUpdates().distinctUntilChanged().collect {result->
                println(result)
                when(result){
                    is LoadResult.Success->{
                        onEvent(CounterOverlayUiEvent.ShowCounter(result.result))
                    }
                    is LoadResult.Error->{
                        println(result.result)
                    }
                }
            }
        }
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