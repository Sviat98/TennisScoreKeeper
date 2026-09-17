package com.bashkevich.tennisscorekeeper.screens.editmatch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.combine
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.UpdateMatchBody
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.navigation.EditMatchRoute
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsRefreshThemeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection

@OptIn(ExperimentalCoroutinesApi::class)
class EditMatchViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val themeRepository: ThemeRepository
) : BaseViewModel<EditMatchState, EditMatchUiEvent, EditMatchAction>() {
    private val matchId: Int = savedStateHandle.toRoute<EditMatchRoute>().matchId

    // Слой 1: матч с веб-сокета (фреймы кэшируются в БД, состояние читается из БД)
    private val networkUpdates = matchRepository.observeMatchUpdatesFromNetworkAndSaveToDb(matchId)

    private val matchFromDb: StateFlow<Match?> = matchRepository.observeMatchById(matchId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Слой 2: пользовательские правки (null = пользователь поле не трогал, берём значение из матча).
    // Хранятся только в памяти: закрыл экран без сохранения — правки пропали
    private val _firstParticipantDisplayName = MutableStateFlow<String?>(null)
    private val _secondParticipantDisplayName = MutableStateFlow<String?>(null)
    private val _selectedThemeId = MutableStateFlow<Int?>(null)
    private val _isSaving = MutableStateFlow(false)

    // Итоговый матч с наложенными правками — то, что показывается на табло и уходит в PUT
    private val editedMatch: StateFlow<Match?> = combine(
        matchFromDb,
        _firstParticipantDisplayName,
        _secondParticipantDisplayName,
        _selectedThemeId,
    ) { match: Match?, firstName: String?, secondName: String?, themeId: Int? ->
        match?.copy(
            firstParticipant = match.firstParticipant.withDisplayName(firstName),
            secondParticipant = match.secondParticipant.withDisplayName(secondName),
            themeId = themeId ?: match.themeId
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // --- Тема: выбранная пользователем поверх темы матча ---
    private val effectiveThemeIdFlow: StateFlow<Int> = combine(
        matchFromDb,
        _selectedThemeId
    ) { match: Match?, selectedId: Int? ->
        selectedId ?: match?.themeId ?: ScoreboardTheme.DEFAULT.id
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScoreboardTheme.DEFAULT.id)

    private val refreshThemeUseCase = MatchDetailsRefreshThemeUseCase(themeRepository, effectiveThemeIdFlow)

    private val themeFetchResult = refreshThemeUseCase.fetchFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val themeFromDb: StateFlow<ScoreboardTheme> = effectiveThemeIdFlow.flatMapLatest { id ->
        themeRepository.observeThemeByIdFromDatabase(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScoreboardTheme.DEFAULT)

    private val themeSelectedState = combine(
        effectiveThemeIdFlow,
        themeFetchResult,
        themeFromDb,
    ) { id: Int, fetchResult: LoadResult<Unit, Throwable>?, dbTheme: ScoreboardTheme ->
        when {
            fetchResult == null -> ThemeComponentState.SelectedThemeState.Loading(id)
            fetchResult is LoadResult.Error -> ThemeComponentState.SelectedThemeState.Error(id)
            else -> ThemeComponentState.SelectedThemeState.Idle(dbTheme)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ThemeComponentState.SelectedThemeState.Idle(null)
    )

    private val _themesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)

    private val themesFromDb: StateFlow<List<ScoreboardTheme>> =
        themeRepository.observeThemesFromDatabase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val themeOptionsState = combine(
        _themesFetchResult,
        themesFromDb,
    ) { result: LoadResult<Unit, Throwable>?, dbData: List<ScoreboardTheme> ->
        when {
            result == null && dbData.isEmpty() -> ThemeComponentState.ThemeOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() -> ThemeComponentState.ThemeOptionsState.Error(
                "Error"
            )

            else -> ThemeComponentState.ThemeOptionsState.Idle(dbData)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ThemeComponentState.ThemeOptionsState.Idle(emptyList())
    )

    private val themeComponentState = combine(
        themeSelectedState, themeOptionsState
    ) { selected: ThemeComponentState.SelectedThemeState, options: ThemeComponentState.ThemeOptionsState ->
        ThemeComponentState(selected, options)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ThemeComponentState(
            ThemeComponentState.SelectedThemeState.Idle(null),
            ThemeComponentState.ThemeOptionsState.Idle(emptyList())
        )
    )

    override val state: StateFlow<EditMatchState> = combine(
        networkUpdates,
        matchFromDb,
        editedMatch,
        matchRepository.observeConnectionState(),
        themeFromDb,
        themeComponentState,
        _isSaving,
        _action,
    ) {
            _: LoadResult<Unit, Throwable>,
            match: Match?,
            edited: Match?,
            connectionState: ConnectionState,
            theme: ScoreboardTheme,
            themeComp: ThemeComponentState,
            isSaving: Boolean,
            action: EditMatchAction?,
        ->
        EditMatchState(
            match = match,
            editedMatch = edited,
            connectionState = connectionState,
            theme = theme,
            themeComponentState = themeComp,
            isSaving = isSaving,
            action = action
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EditMatchState(match = null, editedMatch = null)
    )

    fun onEvent(uiEvent: EditMatchUiEvent) {
        when (uiEvent) {
            is EditMatchUiEvent.ChangeDisplayName -> changeDisplayName(
                participantNumber = uiEvent.participantNumber,
                displayName = uiEvent.displayName
            )

            is EditMatchUiEvent.SelectTheme -> _selectedThemeId.value = uiEvent.themeId

            EditMatchUiEvent.FetchThemes -> fetchThemes()

            is EditMatchUiEvent.RetrySelectedTheme -> refreshThemeUseCase.refresh()

            EditMatchUiEvent.SaveMatch -> saveMatch()
        }
    }

    private fun changeDisplayName(participantNumber: Int, displayName: String) {
        when (participantNumber) {
            1 -> _firstParticipantDisplayName.value = displayName
            2 -> _secondParticipantDisplayName.value = displayName
        }
    }

    private fun fetchThemes() {
        viewModelScope.launch {
            _themesFetchResult.value = null
            _themesFetchResult.value = themeRepository.fetchThemes()
        }
    }

    private fun saveMatch() {
        val edited = editedMatch.value ?: return

        viewModelScope.launch {
            _isSaving.value = true
            val updateMatchBody = UpdateMatchBody(
                firstParticipantDisplayName = edited.firstParticipant.displayName,
                secondParticipantDisplayName = edited.secondParticipant.displayName,
                themeId = edited.themeId.toString(),
            )
            matchRepository.updateMatch(matchId = matchId, updateMatchBody = updateMatchBody)
                .doOnSuccess {
                    _isSaving.value = false
                    sendAction(EditMatchAction.MatchSaved)
                }
                .doOnError { error ->
                    _isSaving.value = false
                    handleError(error)
                }
        }
    }

    private fun TennisParticipantInMatch.withDisplayName(displayName: String?): TennisParticipantInMatch {
        if (displayName == null) return this

        return when (this) {
            is ParticipantInSinglesMatch -> copy(displayName = displayName)
            is ParticipantInDoublesMatch -> copy(displayName = displayName)
        }
    }

    private suspend fun handleError(e: Throwable) {
        println("e = $e")
        when (e) {
            is NetworkException ->
                sendAction(EditMatchAction.ShowError(getString(Res.string.check_internet_connection)))

            is UnauthorizedActionException ->
                sendAction(EditMatchAction.ShowUnauthorizedError)

            else -> sendAction(EditMatchAction.ShowError(e.message ?: "Error"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        matchRepository.closeSession()
    }
}
