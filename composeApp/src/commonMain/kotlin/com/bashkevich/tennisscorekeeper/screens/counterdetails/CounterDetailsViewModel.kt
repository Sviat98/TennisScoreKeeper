package com.bashkevich.tennisscorekeeper.screens.counterdetails

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
import com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class CounterDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val counterRepository: CounterRepository
) :
    BaseViewModel<CounterDetailsState, CounterDetailsUiEvent, CounterDetailsAction>() {

    private val _state = MutableStateFlow(CounterDetailsState.initial())
    override val state: StateFlow<CounterDetailsState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterDetailsAction>
        get() = super.action

    init {
        val counterId = savedStateHandle.toRoute<CounterDetailsRoute>().id

        counterRepository.connectToCounterUpdates(counterId = counterId)

        viewModelScope.launch {
            counterRepository.observeCounterUpdates().distinctUntilChanged().collect {result->
                println(result)
                when(result){
                    is LoadResult.Success->{
                        onEvent(CounterDetailsUiEvent.ShowCounter(result.result))
                    }
                    is LoadResult.Error->{
                       println(result.result)
                    }
                }
            }
        }
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

    override fun onCleared() {
        viewModelScope.launch {
            counterRepository.closeSession()
        }
        super.onCleared()
    }
}