package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import com.bashkevich.tennisscorekeeper.model.match.SimpleMatch

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListUiEvent

@Immutable
sealed class MatchListUiEvent : UiEvent {
    data class ShowMatches(val matches: List<SimpleMatch>): MatchListUiEvent()

}

@Immutable
data class MatchListState(
    val matches: List<SimpleMatch>
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