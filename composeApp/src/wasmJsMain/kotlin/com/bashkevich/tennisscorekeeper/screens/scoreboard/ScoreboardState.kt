package com.bashkevich.tennisscorekeeper.screens.scoreboard

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.Match
import com.bashkevich.tennisscorekeeper.model.match.SAMPLE_MATCH

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class ScoreboardUiEvent : UiEvent {
    data class ShowMatch(val match: Match) : ScoreboardUiEvent()
}

@Immutable
data class ScoreboardState(
    val match: Match
) : UiState {
    companion object {
        fun initial() = ScoreboardState(
            match = SAMPLE_MATCH
        )
    }
}

@Immutable
sealed class ScoreboardAction : UiAction {

}