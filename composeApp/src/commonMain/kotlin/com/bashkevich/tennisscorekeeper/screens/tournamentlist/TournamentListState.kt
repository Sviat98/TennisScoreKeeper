package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class TournamentListUiEvent : UiEvent {
    class ShowTournaments(val tournaments: List<Tournament>): TournamentListUiEvent()
}

@Immutable
data class TournamentListState(
    val tournaments: List<Tournament>
) : UiState {
    companion object {
        fun initial() = TournamentListState(
            tournaments = emptyList()
        )
    }
}

@Immutable
sealed class TournamentListAction : UiAction {

}