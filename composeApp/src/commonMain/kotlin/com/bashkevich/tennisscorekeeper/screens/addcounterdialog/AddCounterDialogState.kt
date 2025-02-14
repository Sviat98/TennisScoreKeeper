package com.bashkevich.tennisscorekeeper.screens.addcounterdialog

import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class AddCounterDialogUiEvent : UiEvent {
}

@Immutable
data class AddCounterDialogState(
    val counterName: String,
) : UiState {
    companion object {
        fun initial() = AddCounterDialogState(
            counterName = ""
        )
    }
}

@Immutable
sealed class AddCounterDialogAction : UiAction {

}