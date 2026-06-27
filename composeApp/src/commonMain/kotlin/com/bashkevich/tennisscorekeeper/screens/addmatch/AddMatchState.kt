package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

sealed interface AddMatchLoadingState {
    data object Loading : AddMatchLoadingState
    data class Content(
        val tournament: Tournament,
        val setsToWin: Int,
        val regularSetComponentState: SetComponentState,
        val decidingSetComponentState: SetComponentState,
        val themeComponentState: ThemeComponentState,
        val participantComponentState: ParticipantComponentState,
        val isAdding: Boolean,
        val dialogState: OpenColorPickerDialogState,
    ) : AddMatchLoadingState
}

data class ParticipantComponentState(
    val options: List<TennisParticipant>,
    val firstParticipant: TennisParticipantInMatch,
    val secondParticipant: TennisParticipantInMatch,
)

@Immutable
sealed class AddMatchUiEvent : UiEvent {
    data object FetchParticipants : AddMatchUiEvent()
    class SelectParticipant(val participantNumber: Int, val participant: TennisParticipant) :
        AddMatchUiEvent()

    class ChangeDisplayName(val participantNumber: Int, val displayName: String) : AddMatchUiEvent()

    class OpenColorPickerDialog(val participantNumber: Int, val colorNumber: Int) : AddMatchUiEvent()

    data object CloseColorPickerDialog : AddMatchUiEvent()

    class SelectPrimaryColor(val participantNumber: Int, val color: androidx.compose.ui.graphics.Color) : AddMatchUiEvent()
    class SelectSecondaryColor(val participantNumber: Int, val color: androidx.compose.ui.graphics.Color?) : AddMatchUiEvent()

    class ChangeSetsToWin(val delta: Int) : AddMatchUiEvent()

    data object FetchThemes : AddMatchUiEvent()
    class SelectTheme(val theme: com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme) : AddMatchUiEvent()

    class FetchSetTemplates(val setTemplateTypeFilter: SetTemplateTypeFilter) : AddMatchUiEvent()
    class SelectSetTemplate(
        val setTemplateTypeFilter: SetTemplateTypeFilter,
        val setTemplate: com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
    ) : AddMatchUiEvent()

    data object AddMatch : AddMatchUiEvent()
}

sealed class OpenColorPickerDialogState {
    data object None : OpenColorPickerDialogState()
    class OpenColorPicker(val participantNumber: Int, val colorNumber: Int) : OpenColorPickerDialogState()
}

@Immutable
data class AddMatchState(
    val loadingState: AddMatchLoadingState,
    val action: AddMatchAction? = null,
) : UiState {
    companion object {
        fun initial() = AddMatchState(
            loadingState = AddMatchLoadingState.Loading,
        )
    }
}

@Immutable
sealed class AddMatchAction : UiAction {
    data object MatchAdded : AddMatchAction()
    data class ShowAddError(val message: String) : AddMatchAction()
    data object ShowUnauthorizedActionError : AddMatchAction()
}
