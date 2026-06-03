package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_DECIDING_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_REGULAR_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.Flow
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

    // Form fields
    private val _tournamentType = MutableStateFlow<TournamentType?>(null)
    private val _regularSetTemplate = MutableStateFlow(EMPTY_REGULAR_SET_TEMPLATE)
    private val _decidingSetTemplate = MutableStateFlow(EMPTY_DECIDING_SET_TEMPLATE)
    private val _selectedTheme = MutableStateFlow<ScoreboardTheme?>(null)
    private val _setsToWin = MutableStateFlow(1)

    private val _isAdding = MutableStateFlow(false)

    // Network fetch results (null = not started / in progress, like TournamentListScreen)
    private val _setTemplatesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)
    private val _themesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)

    // DB observations (property-level, WhileSubscribed)
    private val setTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.ALL)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val themesFromDb: StateFlow<List<ScoreboardTheme>> =
        themeRepository.observeThemesFromDatabase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Derived dropdown substates: combine(fetchResult, dbData) → DropdownLoadState
    private val setTemplatesSubstate = combine(
        _setTemplatesFetchResult,
        setTemplatesFromDb,
    ) { result, dbData ->
        when {
            result == null && dbData.isEmpty() -> DropdownLoadState.Loading
            result is LoadResult.Error && dbData.isEmpty() -> DropdownLoadState.Error("Не удалось загрузить данные")
            else -> DropdownLoadState.Idle(dbData)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DropdownLoadState.Idle(emptyList()))

    private val themesSubstate = combine(
        _themesFetchResult,
        themesFromDb,
    ) { result, dbData ->
        when {
            result == null && dbData.isEmpty() -> DropdownLoadState.Loading
            result is LoadResult.Error && dbData.isEmpty() -> DropdownLoadState.Error("Не удалось загрузить данные")
            else -> DropdownLoadState.Idle(dbData)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DropdownLoadState.Idle(emptyList()))


    // Single state via combine
    override val state: StateFlow<AddTournamentState> = combine(
        _isAdding,
        _tournamentType,
        _regularSetTemplate,
        _decidingSetTemplate,
        _selectedTheme,
        _setsToWin,
        setTemplatesSubstate,
        themesSubstate,
    ) { stateValues ->
        AddTournamentState(
            isAdding = stateValues[0] as Boolean,
            tournamentType = stateValues[1] as TournamentType?,
            regularSetTemplate = stateValues[2] as SetTemplate,
            decidingSetTemplate = stateValues[3] as SetTemplate,
            selectedTheme = stateValues[4] as ScoreboardTheme?,
            setsToWin = stateValues[5] as Int,
            setTemplatesSubstate = stateValues[6] as DropdownLoadState<SetTemplate>,
            themesSubstate = stateValues[7] as DropdownLoadState<ScoreboardTheme>,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddTournamentState.initial()
    )

    val actions: Flow<AddTournamentAction>
        get() = super.action

    fun onEvent(uiEvent: AddTournamentUiEvent) {
        when (uiEvent) {
            is AddTournamentUiEvent.SelectTournamentType -> {
                _tournamentType.value = uiEvent.tournamentType
            }

            is AddTournamentUiEvent.SelectRegularSetTemplate -> {
                _regularSetTemplate.value = uiEvent.setTemplate
            }

            is AddTournamentUiEvent.SelectDecidingSetTemplate -> {
                _decidingSetTemplate.value = uiEvent.setTemplate
            }

            is AddTournamentUiEvent.SelectTheme -> {
                _selectedTheme.value = uiEvent.theme
            }

            is AddTournamentUiEvent.ChangeSetsToWin -> {
                val newSetsToWin = _setsToWin.value + uiEvent.delta
                if (newSetsToWin < 1) return
                _setsToWin.value = newSetsToWin
                if (newSetsToWin < 2) {
                    _regularSetTemplate.value = EMPTY_REGULAR_SET_TEMPLATE
                }
            }

            is AddTournamentUiEvent.FetchSetTemplates -> fetchSetTemplates()
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

    private fun addTournament(
        tournamentName: String,
        tournamentType: TournamentType,
        defaultSetTemplateId: String,
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
                }.doOnError {
                    _isAdding.value = false
                    sendAction(AddTournamentAction.ShowAddError("Не удалось добавить турнир"))
                }
        }
    }

    private fun fetchSetTemplates() {
        viewModelScope.launch {
            _setTemplatesFetchResult.value = null
            _setTemplatesFetchResult.value = setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.ALL)
        }
    }

    private fun fetchThemes() {
        viewModelScope.launch {
            _themesFetchResult.value = null
            _themesFetchResult.value = themeRepository.fetchThemes()
        }
    }
}
