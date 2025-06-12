package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class MatchListUiEvent : UiEvent {
    data class ShowMatches(val matches: List<ShortMatch>): MatchListUiEvent()

}

@Immutable
data class MatchListState(
    val matches: List<ShortMatch>
) : UiState {
    companion object {
        fun initial() = MatchListState(
            matches = emptyList()
        )
    }
}

@Immutable
sealed class MatchListAction : UiAction {

}