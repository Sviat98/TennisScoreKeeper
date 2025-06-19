package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.remote.EMPTY_MATCH_BODY
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.participant.domain.SINGLES_PARTICIPANT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_DECIDING_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_REGULAR_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class AddMatchUiEvent : UiEvent {
    data object FetchParticipants : AddMatchUiEvent()
    class SelectParticipant(val participantNumber: Int, val participant: TennisParticipant) :
        AddMatchUiEvent()

    class ChangeSetsToWin(val delta: Int) : AddMatchUiEvent()

    class FetchSetTemplates(val setTemplateTypeFilter: SetTemplateTypeFilter) : AddMatchUiEvent()
    class SelectSetTemplate(
        val setTemplateTypeFilter: SetTemplateTypeFilter,
        val setTemplate: SetTemplate
    ) : AddMatchUiEvent()

    data object AddMatch : AddMatchUiEvent()
}

@Immutable
data class AddMatchState(
    val isLoading: Boolean,
    val tournament: Tournament,
    val participantOptions: List<TennisParticipant>,
    val firstParticipant: TennisParticipant,
    val secondParticipant: TennisParticipant,
    val firstParticipantDisplayName: ParticipantDisplayNameState,
    val secondParticipantDisplayName: ParticipantDisplayNameState,
    val matchBody: MatchBody,
    val setsToWin: Int,
    val setTemplateOptions: List<SetTemplate>,
    val regularSetTemplate: SetTemplate,
    val decidingSetTemplate: SetTemplate,
) : UiState {
    companion object {
        fun initial() = AddMatchState(
            isLoading = true,
            tournament = TOURNAMENT_DEFAULT,
            participantOptions = emptyList(),
            firstParticipant = SINGLES_PARTICIPANT_DEFAULT,
            secondParticipant = SINGLES_PARTICIPANT_DEFAULT,
            firstParticipantDisplayName = SinglesParticipantDisplayNameState(TextFieldState("")),
            secondParticipantDisplayName = SinglesParticipantDisplayNameState(TextFieldState("")),
            matchBody = EMPTY_MATCH_BODY,
            setsToWin = 1,
            setTemplateOptions = emptyList(),
            regularSetTemplate = EMPTY_REGULAR_SET_TEMPLATE,
            decidingSetTemplate = EMPTY_DECIDING_SET_TEMPLATE
        )
    }
}

@Immutable
sealed class AddMatchAction : UiAction {

}