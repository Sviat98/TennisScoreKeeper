package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.core.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddTournamentViewModel(
    private val tournamentRepository: TournamentRepository,
    private val setTemplateRepository: SetTemplateRepository,
    private val themeRepository: ThemeRepository,
) : BaseViewModel<AddTournamentState, AddTournamentUiEvent, AddTournamentAction>() {

    val tournamentNameState = TextFieldState()

    // Form fields
    private val _tournamentType = MutableStateFlow<TournamentType?>(null)
    private val _regularSetSelectedState = MutableStateFlow<SetComponentState.SelectedSetState>(
        SetComponentState.SelectedSetState.Idle(null)
    )
    private val _decidingSetSelectedState = MutableStateFlow<SetComponentState.SelectedSetState>(
        SetComponentState.SelectedSetState.Idle(null)
    )
    private val _selectedTheme = MutableStateFlow<ScoreboardTheme?>(null)
    private val _setsToWin = MutableStateFlow(1)

    private val _isAdding = MutableStateFlow(false)

    private val _regularSetTemplatesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)
    private val _decidingSetTemplatesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)
    private val _themesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)

    // DB observations
    private val regularSetTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.REGULAR)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val decidingSetTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.DECIDER)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val themesFromDb: StateFlow<List<ScoreboardTheme>> =
        themeRepository.observeThemesFromDatabase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Derived dropdown substates
    private val regularSetOptionsState = combine(
        _regularSetTemplatesFetchResult,
        regularSetTemplatesFromDb,
    ) { result, dbData ->
        when {
            result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() ->
                SetComponentState.SetTemplateOptionsState.Error("There are no items")
            else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SetComponentState.SetTemplateOptionsState.Idle(emptyList()))

    private val decidingSetOptionsState = combine(
        _decidingSetTemplatesFetchResult,
        decidingSetTemplatesFromDb,
    ) { result, dbData ->
        when {
            result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() ->
                SetComponentState.SetTemplateOptionsState.Error("There are no items")
            else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SetComponentState.SetTemplateOptionsState.Idle(emptyList()))

    private val themeOptionsState = combine(
        _themesFetchResult,
        themesFromDb,
    ) { result, dbData ->
        when {
            result == null && dbData.isEmpty() -> ThemeComponentState.ThemeOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() ->
                ThemeComponentState.ThemeOptionsState.Error("There are no items")
            else -> ThemeComponentState.ThemeOptionsState.Idle(dbData)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeComponentState.ThemeOptionsState.Idle(emptyList()))

    // Single state via combine
    override val state: StateFlow<AddTournamentState> = combine(
        _isAdding,
        _tournamentType,
        _regularSetSelectedState,
        _decidingSetSelectedState,
        _selectedTheme,
        _setsToWin,
        regularSetOptionsState,
        decidingSetOptionsState,
        themeOptionsState,
        _action,
    ) { isAdding, tournamentType, regularSetState, decidingSetState, theme, setsToWin, regularSetOptsState, decidingSetOptsState, themeOptsState, action ->
        AddTournamentState(
            isAdding = isAdding,
            tournamentType = tournamentType,
            regularSetComponentState = SetComponentState(
                selectedSetState = regularSetState,
                setOptionsState = regularSetOptsState,
            ),
            decidingSetComponentState = SetComponentState(
                selectedSetState = decidingSetState,
                setOptionsState = decidingSetOptsState,
            ),
            themeComponentState = ThemeComponentState(
                selectedTheme = ThemeComponentState.SelectedThemeState.Idle(theme),
                themeOptionsState = themeOptsState,
            ),
            setsToWin = setsToWin,
            action = action,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddTournamentState.initial()
    )

    fun onEvent(uiEvent: AddTournamentUiEvent) {
        when (uiEvent) {
            is AddTournamentUiEvent.SelectTournamentType -> {
                _tournamentType.value = uiEvent.tournamentType
            }

            is AddTournamentUiEvent.SelectRegularSetTemplate -> {
                _regularSetSelectedState.value = SetComponentState.SelectedSetState.Idle(uiEvent.setTemplate)
            }

            is AddTournamentUiEvent.SelectDecidingSetTemplate -> {
                _decidingSetSelectedState.value = SetComponentState.SelectedSetState.Idle(uiEvent.setTemplate)
            }

            is AddTournamentUiEvent.SelectTheme -> {
                _selectedTheme.value = uiEvent.theme
            }

            is AddTournamentUiEvent.ChangeSetsToWin -> {
                val newSetsToWin = _setsToWin.value + uiEvent.delta
                if (newSetsToWin < 1) return
                _setsToWin.value = newSetsToWin
                if (newSetsToWin < 2) {
                    _regularSetSelectedState.value = SetComponentState.SelectedSetState.Idle(null)
                }
            }

            is AddTournamentUiEvent.FetchSetTemplates -> fetchSetTemplates(uiEvent.filter)
            is AddTournamentUiEvent.FetchThemes -> fetchThemes()

            is AddTournamentUiEvent.AddTournament -> addTournament(
                tournamentName = uiEvent.tournamentName,
                tournamentType = uiEvent.tournamentType,
                defaultSetTemplateId = uiEvent.defaultSetTemplateId,
                decidingSetTemplateId = uiEvent.decidingSetTemplateId,
                themeId = uiEvent.themeId,
                setsToWin = uiEvent.setsToWin,
            )
        }
    }

    private fun fetchSetTemplates(filter: SetTemplateTypeFilter) {
        viewModelScope.launch {
            when (filter) {
                SetTemplateTypeFilter.REGULAR -> {
                    _regularSetTemplatesFetchResult.value = null
                    val result = setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.REGULAR)
                    _regularSetTemplatesFetchResult.value = result
                }
                SetTemplateTypeFilter.DECIDER -> {
                    _decidingSetTemplatesFetchResult.value = null
                    val result = setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.DECIDER)
                    _decidingSetTemplatesFetchResult.value = result
                }
                SetTemplateTypeFilter.ALL -> {
                    _regularSetTemplatesFetchResult.value = null
                    _decidingSetTemplatesFetchResult.value = null
                    val result = setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.ALL)
                    _regularSetTemplatesFetchResult.value = result
                    _decidingSetTemplatesFetchResult.value = result
                }
            }
        }
    }

    private fun fetchThemes() {
        viewModelScope.launch {
            _themesFetchResult.value = null
            val result = themeRepository.fetchThemes()
            _themesFetchResult.value = result
        }
    }

    private fun addTournament(
        tournamentName: String,
        tournamentType: TournamentType,
        defaultSetTemplateId: String?,
        decidingSetTemplateId: String,
        themeId: String,
        setsToWin: Int,
    ) {
        viewModelScope.launch {
            _isAdding.value = true
            val addTournamentBody = AddTournamentBody(
                name = tournamentName,
                type = tournamentType,
                regularSetTemplateId = defaultSetTemplateId,
                decidingSetTemplateId = decidingSetTemplateId,
                themeId = themeId,
                setsToWin = setsToWin,
            )
            tournamentRepository.addTournament(addTournamentBody)
                .doOnSuccess {
                    _isAdding.value = false
                    sendAction(AddTournamentAction.TournamentAdded)
                }.doOnError { throwable ->
                    _isAdding.value = false
                    sendAction(AddTournamentAction.ShowAddError(throwable.message ?: "Не удалось добавить турнир"))
                }
        }
    }
}
