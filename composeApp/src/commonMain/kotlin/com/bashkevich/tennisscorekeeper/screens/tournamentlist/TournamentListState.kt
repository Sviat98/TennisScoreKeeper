package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class TournamentListUiEvent : UiEvent {
    object LoadTournaments: TournamentListUiEvent()
}

@Immutable
data class TournamentListState(
    val loadingState: TournamentListLoadingState,
    val tournaments: List<Tournament>
) : UiState {
    companion object {
        fun initial() = TournamentListState(
            loadingState = TournamentListLoadingState.Loading,
            tournaments = emptyList()
        )
    }
}

sealed interface TournamentListLoadingState{
    object Loading: TournamentListLoadingState
    object Success: TournamentListLoadingState
    object Error: TournamentListLoadingState
}

@Immutable
sealed class TournamentListAction : UiAction {

}