package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_DECIDING_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_REGULAR_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
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
    data class FetchSetTemplates(val filter: SetTemplateTypeFilter) : AddTournamentUiEvent()
    data object FetchThemes : AddTournamentUiEvent()
    class ChangeSetsToWin(val delta: Int) : AddTournamentUiEvent()
    class AddTournament(
        val tournamentName: String,
        val tournamentType: TournamentType,
        val defaultSetTemplateId: String,
        val decidingSetTemplateId: String,
        val themeId: String,
        val setsToWin: Int,
    ) : AddTournamentUiEvent()
}

@Immutable
data class AddTournamentState(
    val tournamentType: TournamentType?,
    val regularSetComponentState: SetComponentState,
    val decidingSetComponentState: SetComponentState,
    val themeComponentState: ThemeComponentState,
    val setsToWin: Int,
    val isAdding: Boolean = false,
    val action: AddTournamentAction? = null,
) : UiState {
    companion object {
        fun initial() = AddTournamentState(
            tournamentType = null,
            regularSetComponentState = SetComponentState(
                selectedSetState = SetComponentState.SelectedSetState.Idle(
                    EMPTY_REGULAR_SET_TEMPLATE
                ),
                setOptionsState = SetComponentState.SetTemplateOptionsState.Loading,
            ),
            decidingSetComponentState = SetComponentState(
                selectedSetState = SetComponentState.SelectedSetState.Idle(
                    EMPTY_DECIDING_SET_TEMPLATE
                ),
                setOptionsState = SetComponentState.SetTemplateOptionsState.Loading,
            ),
            themeComponentState = ThemeComponentState(
                selectedTheme = ThemeComponentState.SelectedThemeState.Idle(
                    ScoreboardTheme.DEFAULT
                ),
                themeOptionsState = ThemeComponentState.ThemeOptionsState.Loading,
            ),
            setsToWin = 1,
            action = null,
        )
    }
}

@Immutable
sealed class AddTournamentAction : UiAction {
    data object TournamentAdded : AddTournamentAction()
    data class ShowAddError(val message: String) : AddTournamentAction()
    data object ShowUnauthorizedError : AddTournamentAction()
}
