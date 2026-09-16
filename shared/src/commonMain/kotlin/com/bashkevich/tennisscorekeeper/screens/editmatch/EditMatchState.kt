package com.bashkevich.tennisscorekeeper.screens.editmatch

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState

@Immutable
sealed class EditMatchUiEvent : UiEvent {
    class ChangeDisplayName(val participantNumber: Int, val displayName: String) :
        EditMatchUiEvent()

    class SelectTheme(val themeId: Int) : EditMatchUiEvent()

    data object FetchThemes : EditMatchUiEvent()

    class RetrySelectedTheme(val themeId: Int) : EditMatchUiEvent()

    data object SaveMatch : EditMatchUiEvent()
}

/**
 * Два слоя состояния:
 * 1. [match] — сам матч, приходящий с веб-сокета (фреймы кэшируются в БД, состояние читается из БД);
 * 2. пользовательские правки (display-имена, тема) — живут только в оперативной памяти
 *    ViewModel и накладываются поверх первого слоя в [editedMatch].
 *    Если пользователь закрыл экран без сохранения — правки теряются.
 */
@Immutable
data class EditMatchState(
    val match: Match?,
    val editedMatch: Match?,
    val connectionState: ConnectionState = ConnectionState.Loading,
    val theme: ScoreboardTheme = ScoreboardTheme.DEFAULT,
    val themeComponentState: ThemeComponentState = ThemeComponentState(
        ThemeComponentState.SelectedThemeState.Idle(null),
        ThemeComponentState.ThemeOptionsState.Idle(emptyList())
    ),
    val isSaving: Boolean = false,
    val action: EditMatchAction? = null
) : UiState

@Immutable
sealed class EditMatchAction : UiAction {
    data object MatchSaved : EditMatchAction()
    data object ShowUnauthorizedError : EditMatchAction()
    data class ShowError(val message: String) : EditMatchAction()
}
