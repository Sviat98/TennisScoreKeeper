package com.bashkevich.tennisscorekeeper.screens.edittournament

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.core.combine
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SET_TEMPLATE_DEFAULT
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.UpdateTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.EditTournamentRoute
import com.bashkevich.tennisscorekeeper.screens.addmatch.TournamentAndMatchSettingsFetchHelper
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection

@OptIn(ExperimentalCoroutinesApi::class)
class EditTournamentViewModel(
    savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository,
    private val themeRepository: ThemeRepository,
    private val setTemplateRepository: SetTemplateRepository
) : BaseViewModel<EditTournamentState, EditTournamentUiEvent, EditTournamentAction>() {
    private val tournamentId: Int = savedStateHandle.toRoute<EditTournamentRoute>().tournamentId

    // Слой 1: турнир из БД (записан при открытии экрана деталей турнира). observeTournamentById
    // эмитит TOURNAMENT_DEFAULT, пока записи нет, — превращаем в null, как matchFromDb на EditMatch
    private val tournamentFromDb: StateFlow<Tournament?> = tournamentRepository
        .observeTournamentById(tournamentId)
        .map { tournament: Tournament -> if (tournament.id == 0) null else tournament }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Слой 2: пользовательские правки (null = поле не трогали, берём значение из турнира).
    // Хранятся только в памяти: закрыл экран без сохранения — правки пропали
    private val _selectedThemeId = MutableStateFlow<Int?>(null)
    private val _selectedRegularSetId = MutableStateFlow<Int?>(null)
    private val _selectedDecidingSetId = MutableStateFlow<Int?>(null)
    private val _selectedSetsToWin = MutableStateFlow<Int?>(null)
    private val _isSaving = MutableStateFlow(false)

    // Правки настроек счёта предобъединены в Triple без нового класса:
    // для combine ровно 5 потоков в stdlib и core.combine одинаковые сигнатуры — вызов станет
    // неоднозначным, если импортировать обе версии (а core нужна для финального combine на 8 потоков)
    private val selectedScoreEdits: StateFlow<Triple<Int?, Int?, Int?>> = combine(
        _selectedRegularSetId,
        _selectedDecidingSetId,
        _selectedSetsToWin,
    ) { regularSetId: Int?, decidingSetId: Int?, setsToWin: Int? ->
        Triple(regularSetId, decidingSetId, setsToWin)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Triple(null, null, null))

    // Итоговый турнир с наложенными правками — то, что показывается на экране и уходит в PUT
    private val editedTournament: StateFlow<Tournament?> = combine(
        tournamentFromDb,
        _selectedThemeId,
        selectedScoreEdits,
    ) { tournament: Tournament?, themeId: Int?, scoreEdits: Triple<Int?, Int?, Int?> ->
        tournament?.copy(
            themeId = themeId ?: tournament.themeId,
            regularSetTemplateId = scoreEdits.first ?: tournament.regularSetTemplateId,
            decidingSetTemplateId = scoreEdits.second ?: tournament.decidingSetTemplateId,
            setsToWin = scoreEdits.third ?: tournament.setsToWin,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // --- Тема: выбранная пользователем поверх темы турнира ---
    private val effectiveThemeIdFlow: StateFlow<Int> = combine(
        tournamentFromDb,
        _selectedThemeId
    ) { tournament: Tournament?, selectedId: Int? ->
        selectedId ?: tournament?.themeId ?: ScoreboardTheme.DEFAULT.id
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScoreboardTheme.DEFAULT.id)

    private val fetchHelper = TournamentAndMatchSettingsFetchHelper(themeRepository, setTemplateRepository)

    private val themeFetchResult = effectiveThemeIdFlow.flatMapLatest { id ->
        fetchHelper.observeThemeByIdFromNetwork(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

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
            result is LoadResult.Error && dbData.isEmpty() -> ThemeComponentState.ThemeOptionsState.Error("Error")

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

    // --- Настройки счёта (sets_to_win + шаблоны сетов): редактируются, как на AddTournament.
    // distinctUntilChanged нужен, чтобы перезаписи турнира в БД не перезапускали загрузку шаблонов
    private val setsToWin: StateFlow<Int> = combine(
        tournamentFromDb,
        _selectedSetsToWin
    ) { tournament: Tournament?, selected: Int? ->
        selected ?: tournament?.setsToWin ?: 1
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    private val regularSetId: StateFlow<Int?> = combine(
        tournamentFromDb,
        _selectedRegularSetId
    ) { tournament: Tournament?, selected: Int? ->
        selected ?: tournament?.regularSetTemplateId
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val decidingSetId: StateFlow<Int> = combine(
        tournamentFromDb,
        _selectedDecidingSetId
    ) { tournament: Tournament?, selected: Int? ->
        selected ?: tournament?.decidingSetTemplateId ?: 0
    }.distinctUntilChanged()
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

    // --- Опции дропдаунов: настройки счёта редактируемы, опции запрашиваются из сети
    // (в отличие от EditMatch, где дропдауны залочены и опции пустые) ---
    private val _regularSetTemplatesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)
    private val _decidingSetTemplatesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)

    private val regularSetTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.REGULAR)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val decidingSetTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.DECIDER)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val regularSetOptionsState = combine(
        _regularSetTemplatesFetchResult,
        regularSetTemplatesFromDb,
    ) { result: LoadResult<Unit, Throwable>?, dbData: List<SetTemplate> ->
        when {
            result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() ->
                SetComponentState.SetTemplateOptionsState.Error("There are no items")
            else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState.SetTemplateOptionsState.Idle(emptyList())
    )

    private val decidingSetOptionsState = combine(
        _decidingSetTemplatesFetchResult,
        decidingSetTemplatesFromDb,
    ) { result: LoadResult<Unit, Throwable>?, dbData: List<SetTemplate> ->
        when {
            result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() ->
                SetComponentState.SetTemplateOptionsState.Error("There are no items")
            else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState.SetTemplateOptionsState.Idle(emptyList())
    )

    private val regularSetComponentState = combine(
        regularSelectedState, regularSetOptionsState
    ) { selected: SetComponentState.SelectedSetState, options: SetComponentState.SetTemplateOptionsState ->
        SetComponentState(selected, options)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState(
            SetComponentState.SelectedSetState.Idle(null),
            SetComponentState.SetTemplateOptionsState.Idle(emptyList())
        )
    )

    private val decidingSetComponentState = combine(
        decidingSelectedState, decidingSetOptionsState
    ) { selected: SetComponentState.SelectedSetState, options: SetComponentState.SetTemplateOptionsState ->
        SetComponentState(selected, options)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState(
            SetComponentState.SelectedSetState.Idle(null),
            SetComponentState.SetTemplateOptionsState.Idle(emptyList())
        )
    )

    override val state: StateFlow<EditTournamentState> = combine(
        tournamentFromDb,
        editedTournament,
        themeComponentState,
        setsToWin,
        regularSetComponentState,
        decidingSetComponentState,
        _isSaving,
        _action,
    ) {
            tournament: Tournament?,
            edited: Tournament?,
            themeComp: ThemeComponentState,
            setsToWin: Int,
            regularSetComp: SetComponentState,
            decidingSetComp: SetComponentState,
            isSaving: Boolean,
            action: EditTournamentAction?,
        ->
        EditTournamentState(
            tournament = tournament,
            editedTournament = edited,
            themeComponentState = themeComp,
            setsToWin = setsToWin,
            regularSetComponentState = regularSetComp,
            decidingSetComponentState = decidingSetComp,
            isSaving = isSaving,
            action = action
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EditTournamentState(tournament = null, editedTournament = null)
    )

    fun onEvent(uiEvent: EditTournamentUiEvent) {
        when (uiEvent) {
            is EditTournamentUiEvent.SelectTheme -> _selectedThemeId.value = uiEvent.themeId

            EditTournamentUiEvent.FetchThemes -> fetchThemes()

            is EditTournamentUiEvent.FetchSetTemplates -> fetchSetTemplates(uiEvent.filter)

            is EditTournamentUiEvent.SelectRegularSetTemplate ->
                _selectedRegularSetId.value = uiEvent.setTemplate.id

            is EditTournamentUiEvent.SelectDecidingSetTemplate ->
                _selectedDecidingSetId.value = uiEvent.setTemplate.id

            is EditTournamentUiEvent.ChangeSetsToWin -> changeSetsToWin(uiEvent.delta)

            is EditTournamentUiEvent.RetrySelectedTheme -> fetchHelper.retryTheme(uiEvent.themeId)

            is EditTournamentUiEvent.RetrySelectedRegularSet -> fetchHelper.retryRegularSet(uiEvent.setId)

            is EditTournamentUiEvent.RetrySelectedDecidingSet -> fetchHelper.retryDecidingSet(uiEvent.setId)

            is EditTournamentUiEvent.SaveTournament -> saveTournament(uiEvent.tournamentName)
        }
    }

    private fun changeSetsToWin(delta: Int) {
        val newSetsToWin = setsToWin.value + delta
        if (newSetsToWin < 1) return
        _selectedSetsToWin.value = newSetsToWin
        if (newSetsToWin < 2) {
            _selectedRegularSetId.value = null
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

    private fun saveTournament(tournamentName: String) {
        val edited = editedTournament.value ?: return
        // COMPLETED — невозвратное состояние: экран открыт на просмотр, сохранение невозможно
        if (edited.status == TournamentStatus.COMPLETED) return

        viewModelScope.launch {
            _isSaving.value = true
            val updateTournamentBody = UpdateTournamentBody(
                name = tournamentName,
                regularSetTemplateId = edited.regularSetTemplateId?.toString(),
                decidingSetTemplateId = edited.decidingSetTemplateId.toString(),
                themeId = edited.themeId.toString(),
                setsToWin = edited.setsToWin,
            )
            tournamentRepository.updateTournament(
                tournamentId = tournamentId,
                updateTournamentBody = updateTournamentBody
            )
                .doOnSuccess {
                    _isSaving.value = false
                    sendAction(EditTournamentAction.TournamentSaved)
                }
                .doOnError { error ->
                    _isSaving.value = false
                    handleError(error)
                }
        }
    }

    private suspend fun handleError(e: Throwable) {
        println("e = $e")
        when (e) {
            is NetworkException ->
                sendAction(EditTournamentAction.ShowError(getString(Res.string.check_internet_connection)))

            is UnauthorizedActionException ->
                sendAction(EditTournamentAction.ShowUnauthorizedError)

            else -> sendAction(EditTournamentAction.ShowError(e.message ?: "Не удалось сохранить турнир"))
        }
    }
}
