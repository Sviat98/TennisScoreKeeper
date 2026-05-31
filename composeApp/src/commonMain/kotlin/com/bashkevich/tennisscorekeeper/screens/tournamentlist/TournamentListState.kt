package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed interface TournamentListState : UiState {
    data object Loading : TournamentListState
    data class Content(
        val tournaments: List<Tournament>,
        val isRefreshing: Boolean = false
    ) : TournamentListState
    data class Error(val message: String) : TournamentListState
}

@Immutable
sealed class TournamentListUiEvent : UiEvent {
    data object RefreshTournaments : TournamentListUiEvent()
    data object Retry : TournamentListUiEvent()
}

@Immutable
sealed class TournamentListAction : UiAction {
    data class ShowRefreshError(val message: String) : TournamentListAction()
}
