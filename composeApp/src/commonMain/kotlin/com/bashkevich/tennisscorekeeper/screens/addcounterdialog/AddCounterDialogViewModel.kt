package com.bashkevich.tennisscorekeeper.screens.addcounterdialog

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AddCounterDialogViewModel(
    private val counterRepository: CounterRepository
) :
    BaseViewModel<AddCounterDialogState, AddCounterDialogUiEvent, AddCounterDialogAction>() {

    private val _state = MutableStateFlow(AddCounterDialogState.initial())
    override val state: StateFlow<AddCounterDialogState>
        get() = _state.asStateFlow()

    val actions: Flow<AddCounterDialogAction>
        get() = super.action

    fun onEvent(uiEvent: AddCounterDialogUiEvent) {
       when(uiEvent){
           is AddCounterDialogUiEvent.AddCounter->{
               viewModelScope.launch {
                   val counterResult = async {
                       counterRepository.addCounter(uiEvent.counterName)
                   }
                   counterResult.await()
               }
           }
       }
    }

    private fun reduceState(reducer: (AddCounterDialogState) -> AddCounterDialogState) {
        _state.update(reducer)
    }

}