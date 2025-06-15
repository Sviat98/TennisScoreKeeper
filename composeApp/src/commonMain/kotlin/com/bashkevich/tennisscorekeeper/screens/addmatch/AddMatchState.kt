package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.participant.domain.SINGLES_PARTICIPANT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class AddMatchUiEvent : UiEvent {
    class ShowTournament(val tournament: Tournament) : AddMatchUiEvent()
}

@Immutable
data class AddMatchState(
    val isLoading: Boolean,
    val tournament: Tournament,
    val firstParticipant: TennisParticipant,
    val secondParticipant: TennisParticipant,
    val firstParticipantDisplayName: String,
    val secondParticipantDisplayName: String,
) : UiState {
    companion object {
        fun initial() = AddMatchState(
            isLoading = true,
            tournament = TOURNAMENT_DEFAULT,
            firstParticipant = SINGLES_PARTICIPANT_DEFAULT,
            secondParticipant = SINGLES_PARTICIPANT_DEFAULT,
            firstParticipantDisplayName = "",
            secondParticipantDisplayName = "",
        )
    }
}

@Immutable
sealed class AddMatchAction : UiAction {

}