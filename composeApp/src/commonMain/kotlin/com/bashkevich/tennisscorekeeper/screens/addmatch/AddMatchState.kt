package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.participant.domain.PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
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

    class ChangeDisplayName(val participantNumber: Int, val displayName: String) : AddMatchUiEvent()

    //Почему используется participantNumber, а не id
    // Могут быть ситуации, когда участник не выбран, но надо выбрать цвет,
    // тогда по id невозможно понять, кудв ставить цвет
    class OpenColorPickerDialog(val participantNumber: Int, val colorNumber: Int) : AddMatchUiEvent()

    data object CloseColorPickerDialog : AddMatchUiEvent()

    class SelectPrimaryColor(val participantNumber: Int, val color: Color) : AddMatchUiEvent()
    class SelectSecondaryColor(val participantNumber: Int, val color: Color?) : AddMatchUiEvent()

    class ChangeSetsToWin(val delta: Int) : AddMatchUiEvent()

    class FetchSetTemplates(val setTemplateTypeFilter: SetTemplateTypeFilter) : AddMatchUiEvent()
    class SelectSetTemplate(
        val setTemplateTypeFilter: SetTemplateTypeFilter,
        val setTemplate: SetTemplate
    ) : AddMatchUiEvent()

    data object AddMatch : AddMatchUiEvent()
}

sealed class OpenColorPickerDialogState {
    data object None : OpenColorPickerDialogState()
    class OpenColorPicker(val participantNumber: Int, val colorNumber: Int) : OpenColorPickerDialogState()
}

@Immutable
data class AddMatchState(
    val isLoading: Boolean,
    val tournament: Tournament,
    val participantOptions: List<TennisParticipant>,
    val firstParticipant: TennisParticipantInMatch,
    val secondParticipant: TennisParticipantInMatch,
    val dialogState: OpenColorPickerDialogState,
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
            firstParticipant = PARTICIPANT_IN_SINGLES_MATCH_DEFAULT,
            secondParticipant = PARTICIPANT_IN_SINGLES_MATCH_DEFAULT,
            dialogState = OpenColorPickerDialogState.None,
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