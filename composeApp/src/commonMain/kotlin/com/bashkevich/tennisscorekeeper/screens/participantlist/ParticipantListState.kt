package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class ParticipantListUiEvent : UiEvent {
    class ShowParticipants(val participants: List<TennisParticipant>) : ParticipantListUiEvent()
}

@Immutable
data class ParticipantListState(
    val participants: List<TennisParticipant>
) : UiState {
    companion object {
        fun initial() = ParticipantListState(
            participants = emptyList()
        )
    }
}

@Immutable
sealed class ParticipantListAction : UiAction {

}