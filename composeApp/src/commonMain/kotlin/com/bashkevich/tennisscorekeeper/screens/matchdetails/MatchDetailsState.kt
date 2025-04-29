package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.Match
import com.bashkevich.tennisscorekeeper.model.match.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.remote.ScoreType

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState


@Immutable
sealed class MatchDetailsUiEvent : UiEvent {
    data class ShowMatch(val match: Match) : MatchDetailsUiEvent()
    class UpdateScore(val matchId: String, val playerId: String, val scoreType: ScoreType) :
        MatchDetailsUiEvent()
}

@Immutable
data class MatchDetailsState(
    val match: Match
) : UiState {
    companion object {
        fun initial() = MatchDetailsState(
            match = SAMPLE_MATCH
        )
    }
}

@Immutable
sealed class MatchDetailsAction : UiAction {

}