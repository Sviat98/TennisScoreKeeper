package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState


enum class ConnectionState {
    Loading, Connected, Disconnected
}

@Immutable
sealed class MatchDetailsUiEvent : UiEvent {
    class SetFirstParticipantToServe(val participantId: Int) :
        MatchDetailsUiEvent()

    class SetFirstPlayerInPairToServe(val playerId: Int) :
        MatchDetailsUiEvent()

    class SetParticipantRetired(val participantId: Int) :
        MatchDetailsUiEvent()

    class ChangeMatchStatus(val status: MatchStatus) :
        MatchDetailsUiEvent()

    class UpdateScore(val participantId: Int, val scoreType: ScoreType) :
        MatchDetailsUiEvent()

    data object UndoPoint : MatchDetailsUiEvent()
    data object RedoPoint : MatchDetailsUiEvent()

    class AttachVideoLink(val videoLink: String) : MatchDetailsUiEvent()
}

@Immutable
data class MatchDetailsState(
    val match: Match,
    val connectionState: ConnectionState = ConnectionState.Loading,
    val action: MatchDetailsAction? = null
) : UiState {
    companion object {
        fun initial() = MatchDetailsState(
            match = SAMPLE_MATCH
        )
    }
}

@Immutable
sealed class MatchDetailsAction : UiAction {
    data object ShowUnauthorizedError : MatchDetailsAction()
}
