package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab

@Immutable
sealed class TournamentUiEvent : UiEvent {
    class ChangeTournamentStatus(val tournamentStatus: TournamentStatus) : TournamentUiEvent()
    class SelectTab(val tournamentTab: TournamentTab): TournamentUiEvent()
}

@Immutable
data class TournamentState(
    val tournament: Tournament,
    val currentTab: TournamentTab
) : UiState {
    companion object {
        fun initial() = TournamentState(
            tournament = TOURNAMENT_DEFAULT,
            currentTab = TournamentTab.Matches
        )
    }
}

@Immutable
sealed class TournamentAction : UiAction {

}