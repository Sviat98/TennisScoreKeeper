package com.bashkevich.tennisscorekeeper.screens.edittournament

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
sealed class EditTournamentUiEvent : UiEvent {
    class SelectTheme(val themeId: Int) : EditTournamentUiEvent()

    data object FetchThemes : EditTournamentUiEvent()

    data class FetchSetTemplates(val filter: SetTemplateTypeFilter) : EditTournamentUiEvent()

    class SelectRegularSetTemplate(val setTemplate: SetTemplate) : EditTournamentUiEvent()

    class SelectDecidingSetTemplate(val setTemplate: SetTemplate) : EditTournamentUiEvent()

    class ChangeSetsToWin(val delta: Int) : EditTournamentUiEvent()

    class RetrySelectedTheme(val themeId: Int) : EditTournamentUiEvent()

    class RetrySelectedRegularSet(val setId: Int) : EditTournamentUiEvent()

    class RetrySelectedDecidingSet(val setId: Int) : EditTournamentUiEvent()

    class SaveTournament(val tournamentName: String) : EditTournamentUiEvent()
}

/**
 * Два слоя состояния:
 * 1. [tournament] — турнир, читаемый из БД (попадает туда при открытии экрана деталей турнира);
 * 2. пользовательские правки (тема, шаблоны сетов, sets_to_win) — живут только в оперативной
 *    памяти ViewModel и накладываются поверх первого слоя в [editedTournament].
 *    Если пользователь закрыл экран без сохранения — правки теряются.
 *
 * Название турнира — TextFieldState на самом экране, в [editedTournament] не входит:
 * оно уходит в PUT отдельным параметром события сохранения.
 */
@Immutable
data class EditTournamentState(
    val tournament: Tournament?,
    val editedTournament: Tournament?,
    val themeComponentState: ThemeComponentState = ThemeComponentState(
        ThemeComponentState.SelectedThemeState.Idle(null),
        ThemeComponentState.ThemeOptionsState.Idle(emptyList())
    ),
    val setsToWin: Int = 1,
    val regularSetComponentState: SetComponentState = SetComponentState(
        SetComponentState.SelectedSetState.Idle(null),
        SetComponentState.SetTemplateOptionsState.Idle(emptyList())
    ),
    val decidingSetComponentState: SetComponentState = SetComponentState(
        SetComponentState.SelectedSetState.Idle(null),
        SetComponentState.SetTemplateOptionsState.Idle(emptyList())
    ),
    val isSaving: Boolean = false,
    val action: EditTournamentAction? = null
) : UiState

@Immutable
sealed class EditTournamentAction : UiAction {
    data object TournamentSaved : EditTournamentAction()
    data object ShowUnauthorizedError : EditTournamentAction()
    data class ShowError(val message: String) : EditTournamentAction()
}
