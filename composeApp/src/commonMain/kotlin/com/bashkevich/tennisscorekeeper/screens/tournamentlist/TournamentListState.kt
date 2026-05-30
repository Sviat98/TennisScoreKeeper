package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class TournamentListUiEvent : UiEvent {
    object LoadTournaments: TournamentListUiEvent()
    object RefreshTournaments: TournamentListUiEvent()
}

@Immutable
data class TournamentListState(
    val loadingState: TournamentListContentState,
    val tournaments: List<Tournament>
) : UiState {
    companion object {
        fun initial() = TournamentListState(
            loadingState = TournamentListContentState.Loading,
            tournaments = emptyList()
        )
    }
}

sealed interface TournamentListContentState{
    object Loading: TournamentListContentState
    object Refreshing: TournamentListContentState
    object Idle: TournamentListContentState
    data class InitialError(val message: String): TournamentListContentState
}

@Immutable
sealed class TournamentListAction : UiAction {
    data class ShowRefreshError(val message: String) : TournamentListAction()
}