package com.bashkevich.tennisscorekeeper.screens.editmatch

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.combine
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.ParticipantInMatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.UpdateMatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.convertToRgbString
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SET_TEMPLATE_DEFAULT
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.navigation.EditMatchRoute
import com.bashkevich.tennisscorekeeper.screens.addmatch.TournamentAndMatchSettingsFetchHelper
import com.bashkevich.tennisscorekeeper.screens.addmatch.OpenColorPickerDialogState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsRefreshThemeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection

@OptIn(ExperimentalCoroutinesApi::class)
class EditMatchViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val themeRepository: ThemeRepository,
    private val setTemplateRepository: SetTemplateRepository
) : BaseViewModel<EditMatchState, EditMatchUiEvent, EditMatchAction>() {
    private val matchId: Int = savedStateHandle.toRoute<EditMatchRoute>().matchId

    // Слой 1: матч с веб-сокета (фреймы кэшируются в БД, состояние читается из БД).
    // networkUpdates подмешан к connectionState ради подписки: его значение не используется
    // (кадры и так пишутся в БД), но WS-сессия должна жить, пока экран подписан на state
    private val networkUpdates = matchRepository.observeMatchUpdatesFromNetworkAndSaveToDb(matchId)

    private val matchFromDb: StateFlow<Match?> = matchRepository.observeMatchById(matchId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val connectionState: StateFlow<ConnectionState> = combine(
        networkUpdates,
        matchRepository.observeConnectionState(),
    ) { _, connectionState: ConnectionState -> connectionState }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ConnectionState.Loading
    )

    // Слой 2: пользовательские правки (null/Unchanged = пользователь поле не трогал, берём значение из матча).
    // Хранятся только в памяти: закрыл экран без сохранения — правки пропали
    private val _firstParticipantEdits = MutableStateFlow(ParticipantEdits())
    private val _secondParticipantEdits = MutableStateFlow(ParticipantEdits())
    private val _dialogState = MutableStateFlow<OpenColorPickerDialogState>(OpenColorPickerDialogState.None)
    private val _selectedThemeId = MutableStateFlow<Int?>(null)
    private val _isSaving = MutableStateFlow(false)

    // Итоговый матч с наложенными правками — то, что показывается на табло и уходит в PUT
    private val editedMatch: StateFlow<Match?> = combine(
        matchFromDb,
        _firstParticipantEdits,
        _secondParticipantEdits,
        _selectedThemeId,
    ) { match: Match?, firstEdits: ParticipantEdits, secondEdits: ParticipantEdits, themeId: Int? ->
        match?.copy(
            firstParticipant = match.firstParticipant.withEdits(firstEdits),
            secondParticipant = match.secondParticipant.withEdits(secondEdits),
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

    // --- Настройки счёта матча (sets_to_win + шаблоны сетов): залочены от редактирования, только показываются.
    // Источник — сам матч (аналог _setsToWin/_regularSetId/_decidingSetId из AddMatch, но read-only из БД);
    // distinctUntilChanged нужен, чтобы счётные WS-кадры не перезапускали загрузку шаблонов
    private val fetchHelper = TournamentAndMatchSettingsFetchHelper(themeRepository, setTemplateRepository)

    private val setsToWin: StateFlow<Int> = matchFromDb
        .map { match: Match? -> match?.setsToWin ?: 1 }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    private val regularSetId: StateFlow<Int?> = matchFromDb
        .map { match: Match? -> match?.regularSetTemplateId }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val decidingSetId: StateFlow<Int> = matchFromDb
        .map { match: Match? -> match?.decidingSetTemplateId ?: 0 }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // --- Fetch results via helper ---
    private val regularSetFetchResult = regularSetId.flatMapLatest { id: Int? ->
        fetchHelper.observeRegularSetByIdFromNetwork(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val decidingSetFetchResult = decidingSetId.flatMapLatest { id: Int ->
        fetchHelper.observeDecidingSetByIdFromNetwork(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // --- DB observations by IDs ---
    private val regularSetFromDb: StateFlow<SetTemplate?> =
        regularSetId.filterNotNull().flatMapLatest { id: Int ->
            setTemplateRepository.observeSetTemplateById(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, SET_TEMPLATE_DEFAULT)

    private val decidingSetFromDb: StateFlow<SetTemplate> =
        decidingSetId.flatMapLatest { id: Int ->
            setTemplateRepository.observeSetTemplateById(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, SET_TEMPLATE_DEFAULT)

    // --- Selected states: Loading/Error/Idle based on fetch result + DB value ---
    private val regularSelectedState = combine(
        regularSetId,
        regularSetFetchResult,
        regularSetFromDb,
    ) { id: Int?, fetchResult: LoadResult<Unit, Throwable>?, dbValue: SetTemplate? ->
        when {
            id == null -> SetComponentState.SelectedSetState.Idle(null)
            fetchResult == null -> SetComponentState.SelectedSetState.Loading(id)
            fetchResult is LoadResult.Error -> SetComponentState.SelectedSetState.Error(id)
            else -> SetComponentState.SelectedSetState.Idle(dbValue)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState.SelectedSetState.Idle(null)
    )

    private val decidingSelectedState = combine(
        decidingSetId,
        decidingSetFetchResult,
        decidingSetFromDb,
    ) { id: Int, fetchResult: LoadResult<Unit, Throwable>?, dbValue: SetTemplate ->
        when (fetchResult) {
            null -> SetComponentState.SelectedSetState.Loading(id)
            is LoadResult.Error -> SetComponentState.SelectedSetState.Error(id)
            else -> SetComponentState.SelectedSetState.Idle(dbValue)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState.SelectedSetState.Idle(null)
    )

    // --- Component states (у AddMatch здесь ещё combine с опциями дропдауна; на EditMatch дропдауны
    // залочены, опции никогда не запрашиваются — подставляем пустой список) ---
    private val regularSetComponentState = regularSelectedState
        .map { selected: SetComponentState.SelectedSetState ->
            SetComponentState(selected, SetComponentState.SetTemplateOptionsState.Idle(emptyList()))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SetComponentState(
                SetComponentState.SelectedSetState.Idle(null),
                SetComponentState.SetTemplateOptionsState.Idle(emptyList())
            )
        )

    private val decidingSetComponentState = decidingSelectedState
        .map { selected: SetComponentState.SelectedSetState ->
            SetComponentState(selected, SetComponentState.SetTemplateOptionsState.Idle(emptyList()))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SetComponentState(
                SetComponentState.SelectedSetState.Idle(null),
                SetComponentState.SetTemplateOptionsState.Idle(emptyList())
            )
        )

    // Компоненты сетов предобъединены в пару без нового класса:
    // итоговый combine ограничен 10 типизированными потоками (core.combine)
    private val setComponents: StateFlow<Pair<SetComponentState, SetComponentState>> = combine(
        regularSetComponentState,
        decidingSetComponentState,
    ) { regular: SetComponentState, deciding: SetComponentState -> regular to deciding }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState(
            SetComponentState.SelectedSetState.Idle(null),
            SetComponentState.SetTemplateOptionsState.Idle(emptyList())
        ) to SetComponentState(
            SetComponentState.SelectedSetState.Idle(null),
            SetComponentState.SetTemplateOptionsState.Idle(emptyList())
        )
    )

    override val state: StateFlow<EditMatchState> = combine(
        matchFromDb,
        editedMatch,
        connectionState,
        themeFromDb,
        themeComponentState,
        setsToWin,
        setComponents,
        _dialogState,
        _isSaving,
        _action,
    ) {
            match: Match?,
            edited: Match?,
            connectionState: ConnectionState,
            theme: ScoreboardTheme,
            themeComp: ThemeComponentState,
            setsToWin: Int,
            setComponents: Pair<SetComponentState, SetComponentState>,
            dialogState: OpenColorPickerDialogState,
            isSaving: Boolean,
            action: EditMatchAction?,
        ->
        EditMatchState(
            match = match,
            editedMatch = edited,
            connectionState = connectionState,
            theme = theme,
            themeComponentState = themeComp,
            setsToWin = setsToWin,
            regularSetComponentState = setComponents.first,
            decidingSetComponentState = setComponents.second,
            dialogState = dialogState,
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

            is EditMatchUiEvent.OpenColorPickerDialog -> openColorPickerDialog(
                participantNumber = uiEvent.participantNumber,
                colorNumber = uiEvent.colorNumber
            )

            EditMatchUiEvent.CloseColorPickerDialog -> closeColorPickerDialog()

            is EditMatchUiEvent.SelectPrimaryColor -> selectPrimaryColor(
                participantNumber = uiEvent.participantNumber,
                color = uiEvent.color
            )

            is EditMatchUiEvent.SelectSecondaryColor -> selectSecondaryColor(
                participantNumber = uiEvent.participantNumber,
                color = uiEvent.color
            )

            is EditMatchUiEvent.SelectTheme -> _selectedThemeId.value = uiEvent.themeId

            EditMatchUiEvent.FetchThemes -> fetchThemes()

            is EditMatchUiEvent.RetrySelectedTheme -> refreshThemeUseCase.refresh()

            is EditMatchUiEvent.RetrySelectedRegularSet -> fetchHelper.retryRegularSet(uiEvent.setId)

            is EditMatchUiEvent.RetrySelectedDecidingSet -> fetchHelper.retryDecidingSet(uiEvent.setId)

            EditMatchUiEvent.SaveMatch -> saveMatch()
        }
    }

    private fun changeDisplayName(participantNumber: Int, displayName: String) {
        when (participantNumber) {
            1 -> _firstParticipantEdits.update { it.copy(displayName = displayName) }
            2 -> _secondParticipantEdits.update { it.copy(displayName = displayName) }
        }
    }

    private fun openColorPickerDialog(participantNumber: Int, colorNumber: Int) {
        _dialogState.value =
            OpenColorPickerDialogState.OpenColorPicker(participantNumber, colorNumber)
    }

    private fun closeColorPickerDialog() {
        _dialogState.value = OpenColorPickerDialogState.None
    }

    private fun selectPrimaryColor(participantNumber: Int, color: Color) {
        when (participantNumber) {
            1 -> _firstParticipantEdits.update { it.copy(primaryColor = color) }
            2 -> _secondParticipantEdits.update { it.copy(primaryColor = color) }
        }
        _dialogState.value = OpenColorPickerDialogState.None
    }

    private fun selectSecondaryColor(participantNumber: Int, color: Color?) {
        val secondaryColorEdit = if (color != null) {
            SecondaryColorEdit.Set(color)
        } else {
            SecondaryColorEdit.Removed
        }
        when (participantNumber) {
            1 -> _firstParticipantEdits.update { it.copy(secondaryColor = secondaryColorEdit) }
            2 -> _secondParticipantEdits.update { it.copy(secondaryColor = secondaryColorEdit) }
        }
        _dialogState.value = OpenColorPickerDialogState.None
    }

    private fun fetchThemes() {
        viewModelScope.launch {
            _themesFetchResult.value = null
            _themesFetchResult.value = themeRepository.fetchThemes()
        }
    }

    private fun saveMatch() {
        val edited = editedMatch.value ?: return
        // COMPLETED — невозвратное состояние: экран открыт на просмотр, сохранение невозможно
        if (edited.status == MatchStatus.COMPLETED) return

        viewModelScope.launch {
            _isSaving.value = true
            val updateMatchBody = UpdateMatchBody(
                firstParticipant = edited.firstParticipant.toParticipantInMatchBody(),
                secondParticipant = edited.secondParticipant.toParticipantInMatchBody(),
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

    private fun TennisParticipantInMatch.toParticipantInMatchBody() = ParticipantInMatchBody(
        id = id.toString(),
        displayName = displayName,
        primaryColor = primaryColor.convertToRgbString(),
        secondaryColor = secondaryColor?.convertToRgbString()
    )

    private fun TennisParticipantInMatch.withEdits(edits: ParticipantEdits): TennisParticipantInMatch {
        val editedSecondaryColor = when (val secondaryColorEdit = edits.secondaryColor) {
            is SecondaryColorEdit.Unchanged -> secondaryColor
            is SecondaryColorEdit.Set -> secondaryColorEdit.color
            is SecondaryColorEdit.Removed -> null
        }

        return when (this) {
            is ParticipantInSinglesMatch -> copy(
                displayName = edits.displayName ?: displayName,
                primaryColor = edits.primaryColor ?: primaryColor,
                secondaryColor = editedSecondaryColor
            )

            is ParticipantInDoublesMatch -> copy(
                displayName = edits.displayName ?: displayName,
                primaryColor = edits.primaryColor ?: primaryColor,
                secondaryColor = editedSecondaryColor
            )
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
}

/**
 * Пользовательские правки одного участника, накладываемые поверх данных матча.
 * null = поле не трогали, берём значение из матча.
 */
private data class ParticipantEdits(
    val displayName: String? = null,
    val primaryColor: Color? = null,
    val secondaryColor: SecondaryColorEdit = SecondaryColorEdit.Unchanged,
)

// Второй цвет nullable, поэтому «не трогал» и «убрал» — разные состояния
private sealed interface SecondaryColorEdit {
    data object Unchanged : SecondaryColorEdit
    data class Set(val color: Color) : SecondaryColorEdit
    data object Removed : SecondaryColorEdit
}
