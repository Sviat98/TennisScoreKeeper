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
    val tournamentName: TextFieldState,
    val tournamentType: TournamentType?
) : UiState {
    companion object {
        fun initial() = AddTournamentState(
            tournamentName = TextFieldState(initialText = ""),
            tournamentType = null
        )
    }
}

@Immutable
sealed class AddTournamentAction : UiAction {

}