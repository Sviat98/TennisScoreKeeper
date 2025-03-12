package com.bashkevich.tennisscorekeeper.screens.addcounterdialog

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class AddCounterDialogUiEvent : UiEvent {
    class AddCounter(val counterName: String): AddCounterDialogUiEvent()
}

@Immutable
data class AddCounterDialogState(
    val counterName: TextFieldState,
) : UiState {
    companion object {
        fun initial() = AddCounterDialogState(
            counterName = TextFieldState("")
        )
    }
}

@Immutable
sealed class AddCounterDialogAction : UiAction {

}