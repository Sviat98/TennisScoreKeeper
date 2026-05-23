package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_DECIDING_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_REGULAR_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.theme.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class AddTournamentUiEvent : UiEvent {
    class SelectTournamentType(val tournamentType: TournamentType) : AddTournamentUiEvent()
    class SelectRegularSetTemplate(val setTemplate: SetTemplate) : AddTournamentUiEvent()
    class SelectDecidingSetTemplate(val setTemplate: SetTemplate) : AddTournamentUiEvent()
    class SelectTheme(val theme: ScoreboardTheme) : AddTournamentUiEvent()
    class AddTournament(
        val tournamentName: String,
        val tournamentType: TournamentType,
        val defaultSetTemplateId: String,
        val decidingSetTemplateId: String,
        val themeId: String,
    ) : AddTournamentUiEvent()
}

@Immutable
data class AddTournamentState(
    val tournamentAddingSubstate: TournamentAddingSubstate,
    val tournamentName: TextFieldState,
    val tournamentType: TournamentType?,
    val setTemplateOptions: List<SetTemplate>,
    val regularSetTemplate: SetTemplate,
    val decidingSetTemplate: SetTemplate,
    val setTemplatesLoading: Boolean,
    val themeOptions: List<ScoreboardTheme>,
    val selectedTheme: ScoreboardTheme?,
    val themesLoading: Boolean,
) : UiState {
    companion object {
        fun initial() = AddTournamentState(
            tournamentAddingSubstate = TournamentAddingSubstate.Idle,
            tournamentName = TextFieldState(initialText = ""),
            tournamentType = null,
            setTemplateOptions = emptyList(),
            regularSetTemplate = EMPTY_REGULAR_SET_TEMPLATE,
            decidingSetTemplate = EMPTY_DECIDING_SET_TEMPLATE,
            setTemplatesLoading = true,
            themeOptions = emptyList(),
            selectedTheme = null,
            themesLoading = true,
        )
    }
}

@Immutable
sealed class TournamentAddingSubstate {
    data object Idle : TournamentAddingSubstate()
    data object Loading : TournamentAddingSubstate()
    data object Success : TournamentAddingSubstate()
    data class Error(val message: String) : TournamentAddingSubstate()
}

@Immutable
sealed class AddTournamentAction : UiAction {

}
