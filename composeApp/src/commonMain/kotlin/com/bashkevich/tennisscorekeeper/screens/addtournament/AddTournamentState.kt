package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class AddTournamentUiEvent : UiEvent {
    class SelectTournamentType(val tournamentType: TournamentType) : AddTournamentUiEvent()
    class AddTournament(val tournamentName: String, val tournamentType: TournamentType) :
        AddTournamentUiEvent()
}

@Immutable
data class AddTournamentState(
    val addTournamentSubstate: AddTournamentSubstate,
    val tournamentName: TextFieldState,
    val tournamentType: TournamentType?
) : UiState {
    companion object {
        fun initial() = AddTournamentState(
            addTournamentSubstate = AddTournamentSubstate.Idle,
            tournamentName = TextFieldState(initialText = ""),
            tournamentType = null
        )
    }
}

@Immutable
sealed class AddTournamentSubstate {
    data object Idle : AddTournamentSubstate()
    data object Loading : AddTournamentSubstate()
    data object Success : AddTournamentSubstate()
    data class Error(val message: String) : AddTournamentSubstate()
}

@Immutable
sealed class AddTournamentAction : UiAction {

}