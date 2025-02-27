package com.bashkevich.tennisscorekeeper.screens.counterdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.counter.COUNTER_DEFAULT
import com.bashkevich.tennisscorekeeper.model.counter.Counter

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class CounterDetailsUiEvent : UiEvent {
    class ShowCounter(val counter: Counter): CounterDetailsUiEvent()
    class ChangeCounterValue(val counterId: String,val delta: Int): CounterDetailsUiEvent()
}

@Immutable
data class CounterDetailsState(
    val counter: Counter
) : UiState {
    companion object {
        fun initial() = CounterDetailsState(
            counter = COUNTER_DEFAULT
        )
    }
}

@Immutable
sealed class CounterDetailsAction : UiAction {

}