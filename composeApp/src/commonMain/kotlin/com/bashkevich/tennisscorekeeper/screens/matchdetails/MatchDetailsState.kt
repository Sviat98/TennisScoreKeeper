package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState


@Immutable
sealed class MatchDetailsUiEvent : UiEvent {
    class ShowMatch(val match: Match) : MatchDetailsUiEvent()
    class SetFirstParticipantToServe(val participantId: String) :
        MatchDetailsUiEvent()
    class SetFirstPlayerInPairToServe(val playerId: String) :
        MatchDetailsUiEvent()
    class ChangeMatchStatus(val status: MatchStatus) :
        MatchDetailsUiEvent()
    class UpdateScore(val participantId: String, val scoreType: ScoreType) :
        MatchDetailsUiEvent()
    data object UndoPoint : MatchDetailsUiEvent()
    data object RedoPoint : MatchDetailsUiEvent()
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