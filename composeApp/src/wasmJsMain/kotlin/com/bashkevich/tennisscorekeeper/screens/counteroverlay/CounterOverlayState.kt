package com.bashkevich.tennisscorekeeper.screens.counteroverlay

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.counter.COUNTER_DEFAULT
import com.bashkevich.tennisscorekeeper.model.counter.Counter

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class CounterOverlayUiEvent : UiEvent {
    class ShowCounter(val counter: Counter): CounterOverlayUiEvent()
}

@Immutable
data class CounterOverlayState(
    val counter: Counter
) : UiState {
    companion object {
        fun initial() = CounterOverlayState(
            counter = COUNTER_DEFAULT
        )
    }
}

@Immutable
sealed class CounterOverlayAction : UiAction {

}