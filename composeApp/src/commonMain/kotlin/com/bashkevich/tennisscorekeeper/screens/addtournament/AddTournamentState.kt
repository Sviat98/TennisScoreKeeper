package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.runtime.Immutable
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
sealed class DropdownLoadState<out T> {
    data class Idle<out T>(val data: List<T>) : DropdownLoadState<T>()
    data object Loading : DropdownLoadState<Nothing>()
    data class Error(val message: String) : DropdownLoadState<Nothing>()
}

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
    val regularSetTemplate: SetTemplate,
    val decidingSetTemplate: SetTemplate,
    val selectedTheme: ScoreboardTheme?,
    val setsToWin: Int,
    val isAdding: Boolean = false,
    val setTemplatesSubstate: DropdownLoadState<SetTemplate>,
    val themesSubstate: DropdownLoadState<ScoreboardTheme>,
) : UiState {
    companion object {
        fun initial() = AddTournamentState(
            tournamentType = null,
            regularSetTemplate = EMPTY_REGULAR_SET_TEMPLATE,
            decidingSetTemplate = EMPTY_DECIDING_SET_TEMPLATE,
            selectedTheme = null,
            setsToWin = 1,
            setTemplatesSubstate = DropdownLoadState.Idle(emptyList<SetTemplate>()),
            themesSubstate = DropdownLoadState.Idle(emptyList<ScoreboardTheme>()),
        )
    }
}

@Immutable
sealed class AddTournamentAction : UiAction {
    data object TournamentAdded : AddTournamentAction()
    data class ShowAddError(val message: String) : AddTournamentAction()
}

//fun <T> DropdownLoadState<T>.items(): List<T> =
//    (this as? DropdownLoadState.Idle)?.data ?: emptyList()
