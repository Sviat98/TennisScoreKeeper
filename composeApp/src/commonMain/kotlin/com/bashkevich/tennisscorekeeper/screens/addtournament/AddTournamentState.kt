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
    val tournamentAddingSubstate: TournamentAddingSubstate,
    val tournamentName: TextFieldState,
    val tournamentType: TournamentType?
) : UiState {
    companion object {
        fun initial() = AddTournamentState(
            tournamentAddingSubstate = TournamentAddingSubstate.Idle,
            tournamentName = TextFieldState(initialText = ""),
            tournamentType = null
        )
    }
}

@Immutable
sealed class TournamentAddingSubstate {
    data object Idle : TournamentAddingSubstate()
    data object Loading : TournamentAddingSubstate()
    data object Success : TournamentAddingSubstate()
    data class Error(val message: String) : TournamentAddingSubstate()
}

@Immutable
sealed class AddTournamentAction : UiAction {

}