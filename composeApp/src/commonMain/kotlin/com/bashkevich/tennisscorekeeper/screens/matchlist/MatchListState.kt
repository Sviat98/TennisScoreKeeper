package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class MatchListUiEvent : UiEvent {
    data class ShowMatches(val matches: List<ShortMatch>): MatchListUiEvent()
    class SetTournamentId(val tournamentId: String) : MatchListUiEvent()
}

@Immutable
data class MatchListState(
    val tournamentId: String,
    val matches: List<ShortMatch>
) : UiState {
    companion object {
        fun initial() = MatchListState(
            tournamentId = "",
            matches = emptyList()
        )
    }
}

@Immutable
sealed class MatchListAction : UiAction {

}