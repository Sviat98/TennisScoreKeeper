package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
data class TournamentListState(
    val loadingState: TournamentListLoadingState = TournamentListLoadingState.Loading,
    val action: TournamentListAction? = null
) : UiState {
    companion object {
        fun initial() = TournamentListState(
            loadingState = TournamentListLoadingState.Loading,
            action = null
        )
    }
}

@Immutable
sealed interface TournamentListLoadingState : UiState {
    data object Loading : TournamentListLoadingState
    data class Content(
        val tournaments: List<Tournament>,
        val isRefreshing: Boolean = false,
    ) : TournamentListLoadingState

    data class Error(val message: String) : TournamentListLoadingState
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
