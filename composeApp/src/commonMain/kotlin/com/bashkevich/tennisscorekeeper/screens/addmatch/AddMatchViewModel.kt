package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.core.combine
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.check_internet_connection
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.ParticipantInMatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.convertToRgbString
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.SinglesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SET_TEMPLATE_DEFAULT
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AddMatchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val tournamentRepository: TournamentRepository,
    private val participantRepository: ParticipantRepository,
    private val setTemplateRepository: SetTemplateRepository,
    private val themeRepository: ThemeRepository,
) : BaseViewModel<AddMatchState, AddMatchUiEvent, AddMatchAction>() {

    private val tournamentId: Int = savedStateHandle.toRoute<AddMatchRoute>().tournamentId

    // --- Tournament from DB (no fetchTournamentById, tournament is already cached) ---
    private val tournamentFromDb: StateFlow<Tournament> =
        tournamentRepository.observeTournamentById(tournamentId)
            .filter { it != TOURNAMENT_DEFAULT }
            .onEach { tournament ->
                    _regularSetId.value = tournament.regularSetTemplateId
                    _decidingSetId.value = tournament.decidingSetTemplateId
                    _themeId.value = tournament.themeId
                    _setsToWin.value = tournament.setsToWin
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TOURNAMENT_DEFAULT)

    // --- Selected IDs (single source of truth) ---
    private val _regularSetId = MutableStateFlow(tournamentFromDb.value.regularSetTemplateId)
    private val _decidingSetId = MutableStateFlow(tournamentFromDb.value.decidingSetTemplateId)
    private val _themeId = MutableStateFlow(tournamentFromDb.value.themeId)
    private val _setsToWin = MutableStateFlow(tournamentFromDb.value.setsToWin)

    // --- Fetch helper ---
    private val fetchHelper = AddMatchFetchHelper(themeRepository, setTemplateRepository)

    // --- Fetch results via helper ---
    private val regularSetFetchResult = tournamentFromDb.flatMapLatest { tournament ->
        fetchHelper.observeRegularSetByIdFromNetwork(tournament.regularSetTemplateId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val decidingSetFetchResult = tournamentFromDb.flatMapLatest { tournament ->
        fetchHelper.observeDecidingSetByIdFromNetwork(tournament.decidingSetTemplateId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val themeFetchResult = tournamentFromDb.flatMapLatest { tournament ->
        fetchHelper.observeThemeByIdFromNetwork(tournament.themeId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // --- DB observations by selected IDs (reactive to user selection changes) ---
    private val regularSetFromDb: StateFlow<SetTemplate?> =
        _regularSetId.filterNotNull().flatMapLatest { id ->
             setTemplateRepository.observeSetTemplateById(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, SET_TEMPLATE_DEFAULT)

    private val decidingSetFromDb: StateFlow<SetTemplate> =
        _decidingSetId.flatMapLatest { id ->
            setTemplateRepository.observeSetTemplateById(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, SET_TEMPLATE_DEFAULT)

    private val themeFromDb: StateFlow<ScoreboardTheme> =
        _themeId.flatMapLatest { id ->
            themeRepository.observeThemeByIdFromDatabase(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, ScoreboardTheme.DEFAULT)

    // --- Selected states: Loading/Error/Idle based on fetch result + DB value ---
    private val regularSelectedState = combine(
        _regularSetId,
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
        _decidingSetId,
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

    private val themeSelectedState = combine(
        _themeId,
        themeFetchResult,
        themeFromDb,
    ) { id: Int, fetchResult: LoadResult<Unit, Throwable>?, dbValue: ScoreboardTheme ->
        when (fetchResult) {
            null -> ThemeComponentState.SelectedThemeState.Loading(id)
            is LoadResult.Error -> ThemeComponentState.SelectedThemeState.Error(id)
            else -> ThemeComponentState.SelectedThemeState.Idle(dbValue)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ThemeComponentState.SelectedThemeState.Idle(null)
    )

    // --- Options fetch results (on-demand via FetchSetTemplates/FetchThemes) ---
    private val _regularSetOptionsFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)
    private val _decidingSetOptionsFetchResult =
        MutableStateFlow<LoadResult<Unit, Throwable>?>(null)
    private val _themesFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)

    // --- DB observations for options ---
    private val regularSetTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.REGULAR)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val decidingSetTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.DECIDER)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val themesFromDb: StateFlow<List<ScoreboardTheme>> =
        themeRepository.observeThemesFromDatabase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // --- Options states (same pattern as AddTournament) ---
    private val regularSetOptionsState = combine(
        _regularSetOptionsFetchResult,
        regularSetTemplatesFromDb,
    ) { result: LoadResult<Unit, Throwable>?, dbData: List<SetTemplate> ->
        when {
            result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Error(
                "Error"
            )

            else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState.SetTemplateOptionsState.Idle(emptyList())
    )

    private val decidingSetOptionsState = combine(
        _decidingSetOptionsFetchResult,
        decidingSetTemplatesFromDb,
    ) { result: LoadResult<Unit, Throwable>?, dbData: List<SetTemplate> ->
        when {
            result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
            result is LoadResult.Error && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Error(
                "Error"
            )

            else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SetComponentState.SetTemplateOptionsState.Idle(emptyList())
    )

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

    // --- Component states (combine selected + options) ---
    private val regularSetComponentState = combine(
        regularSelectedState, regularSetOptionsState
    ) { selected: SetComponentState.SelectedSetState, options: SetComponentState.SetTemplateOptionsState ->
        SetComponentState(selected, options)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), SetComponentState(
            SetComponentState.SelectedSetState.Idle(null),
            SetComponentState.SetTemplateOptionsState.Idle(emptyList())
        )
    )

    private val decidingSetComponentState = combine(
        decidingSelectedState, decidingSetOptionsState
    ) { selected: SetComponentState.SelectedSetState, options: SetComponentState.SetTemplateOptionsState ->
        SetComponentState(selected, options)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), SetComponentState(
            SetComponentState.SelectedSetState.Idle(null),
            SetComponentState.SetTemplateOptionsState.Idle(emptyList())
        )
    )

    private val themeComponentState = combine(
        themeSelectedState, themeOptionsState
    ) { selected: ThemeComponentState.SelectedThemeState, options: ThemeComponentState.ThemeOptionsState ->
        ThemeComponentState(selected, options)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeComponentState(
            ThemeComponentState.SelectedThemeState.Idle(null),
            ThemeComponentState.ThemeOptionsState.Idle(emptyList())
        )
    )

    // --- User input ---
    private val _firstParticipant =
        MutableStateFlow<TennisParticipantInMatch>(PARTICIPANT_IN_SINGLES_MATCH_DEFAULT)
    private val _secondParticipant =
        MutableStateFlow<TennisParticipantInMatch>(PARTICIPANT_IN_SINGLES_MATCH_DEFAULT)
    private val _isAdding = MutableStateFlow(false)
    private val _dialogState =
        MutableStateFlow<OpenColorPickerDialogState>(OpenColorPickerDialogState.None)

    // --- DB observations for participants ---
    private val participantsFromDb: StateFlow<List<TennisParticipant>> =
        participantRepository.observeParticipantsForTournament(tournamentId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // --- Participant data ---
    private val participantData: StateFlow<ParticipantComponentState> = combine(
        participantsFromDb,
        _firstParticipant,
        _secondParticipant,
    ) { options: List<TennisParticipant>, first: TennisParticipantInMatch, second: TennisParticipantInMatch ->
        ParticipantComponentState(options, first, second)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ParticipantComponentState(
            emptyList(), PARTICIPANT_IN_SINGLES_MATCH_DEFAULT, PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
        )
    )

    // --- Final state (9 params — within core.combine limit of 10) ---
    override val state: StateFlow<AddMatchState> = combine(
        tournamentFromDb,
        regularSetComponentState,
        decidingSetComponentState,
        themeComponentState,
        participantData,
        _setsToWin,
        _isAdding,
        _dialogState,
        _action,
    ) {
            tournament: Tournament,
            regularComp: SetComponentState,
            decidingComp: SetComponentState,
            themeComp: ThemeComponentState,
            participantState: ParticipantComponentState,
            setsToWin: Int,
            isAdding: Boolean,
            dialogState: OpenColorPickerDialogState,
            action: AddMatchAction?,
        ->
        if (tournament == TOURNAMENT_DEFAULT) {
            AddMatchState(loadingState = AddMatchLoadingState.Loading, action = action)
        } else {
            AddMatchState(
                loadingState = AddMatchLoadingState.Content(
                    tournament = tournament,
                    setsToWin = setsToWin,
                    regularSetComponentState = regularComp,
                    decidingSetComponentState = decidingComp,
                    themeComponentState = themeComp,
                    participantComponentState = participantState,
                    isAdding = isAdding,
                    dialogState = dialogState,
                ),
                action = action,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddMatchState.initial()
    )

    fun onEvent(uiEvent: AddMatchUiEvent) {
        when (uiEvent) {
            is AddMatchUiEvent.FetchParticipants -> fetchParticipants()
            is AddMatchUiEvent.SelectParticipant -> selectParticipant(
                participantNumber = uiEvent.participantNumber,
                participant = uiEvent.participant
            )

            is AddMatchUiEvent.ChangeDisplayName -> changeDisplayName(
                participantNumber = uiEvent.participantNumber,
                displayName = uiEvent.displayName
            )

            is AddMatchUiEvent.SelectPrimaryColor -> selectPrimaryColor(
                participantNumber = uiEvent.participantNumber,
                color = uiEvent.color
            )

            is AddMatchUiEvent.SelectSecondaryColor -> selectSecondaryColor(
                participantNumber = uiEvent.participantNumber,
                color = uiEvent.color
            )

            is AddMatchUiEvent.OpenColorPickerDialog -> {
                _dialogState.value = OpenColorPickerDialogState.OpenColorPicker(
                    participantNumber = uiEvent.participantNumber,
                    colorNumber = uiEvent.colorNumber
                )
            }

            AddMatchUiEvent.CloseColorPickerDialog -> {
                _dialogState.value = OpenColorPickerDialogState.None
            }

            is AddMatchUiEvent.ChangeSetsToWin -> {
                val new = _setsToWin.value + uiEvent.delta
                if (new < 1) return
                _setsToWin.value = new
                if (new < 2) {
                    _regularSetId.value = null
                }
            }

            is AddMatchUiEvent.FetchThemes -> fetchThemes()
            is AddMatchUiEvent.SelectTheme -> {
                _themeId.value = uiEvent.theme.id
            }

            is AddMatchUiEvent.RetrySelectedTheme -> {
                fetchHelper.retryTheme(uiEvent.themeId)
            }

            is AddMatchUiEvent.FetchSetTemplates -> fetchSetTemplates(uiEvent.setTemplateTypeFilter)
            is AddMatchUiEvent.SelectSetTemplate -> {
                when (uiEvent.setTemplateTypeFilter) {
                    SetTemplateTypeFilter.REGULAR -> _regularSetId.value = uiEvent.setTemplate.id
                    SetTemplateTypeFilter.DECIDER -> _decidingSetId.value = uiEvent.setTemplate.id
                    else -> Unit
                }
            }

            is AddMatchUiEvent.RetrySelectedRegularSet -> {
                fetchHelper.retryRegularSet(uiEvent.setId)
            }

            is AddMatchUiEvent.RetrySelectedDecidingSet -> {
                fetchHelper.retryDecidingSet(uiEvent.setId)
            }

            is AddMatchUiEvent.AddMatch -> addMatch()
        }
    }

    private fun changeDisplayName(participantNumber: Int, displayName: String) {
        when (participantNumber) {
            1 -> _firstParticipant.value = _firstParticipant.value.let { p ->
                when (p) {
                    is ParticipantInSinglesMatch -> p.copy(displayName = displayName)
                    is ParticipantInDoublesMatch -> p.copy(displayName = displayName)
                }
            }

            2 -> _secondParticipant.value = _secondParticipant.value.let { p ->
                when (p) {
                    is ParticipantInSinglesMatch -> p.copy(displayName = displayName)
                    is ParticipantInDoublesMatch -> p.copy(displayName = displayName)
                }
            }
        }
    }

    private fun selectPrimaryColor(participantNumber: Int, color: Color) {
        when (participantNumber) {
            1 -> _firstParticipant.value = _firstParticipant.value.let { p ->
                when (p) {
                    is ParticipantInSinglesMatch -> p.copy(primaryColor = color)
                    is ParticipantInDoublesMatch -> p.copy(primaryColor = color)
                }
            }

            2 -> _secondParticipant.value = _secondParticipant.value.let { p ->
                when (p) {
                    is ParticipantInSinglesMatch -> p.copy(primaryColor = color)
                    is ParticipantInDoublesMatch -> p.copy(primaryColor = color)
                }
            }
        }
        _dialogState.value = OpenColorPickerDialogState.None
    }

    private fun selectSecondaryColor(participantNumber: Int, color: Color?) {
        when (participantNumber) {
            1 -> _firstParticipant.value = _firstParticipant.value.let { p ->
                when (p) {
                    is ParticipantInSinglesMatch -> p.copy(secondaryColor = color)
                    is ParticipantInDoublesMatch -> p.copy(secondaryColor = color)
                }
            }

            2 -> _secondParticipant.value = _secondParticipant.value.let { p ->
                when (p) {
                    is ParticipantInSinglesMatch -> p.copy(secondaryColor = color)
                    is ParticipantInDoublesMatch -> p.copy(secondaryColor = color)
                }
            }
        }
        _dialogState.value = OpenColorPickerDialogState.None
    }

    private fun fetchSetTemplates(filter: SetTemplateTypeFilter) {
        viewModelScope.launch {
            when (filter) {
                SetTemplateTypeFilter.REGULAR -> {
                    _regularSetOptionsFetchResult.value = null
                    val result =
                        setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.REGULAR)
                    _regularSetOptionsFetchResult.value = result
                }

                SetTemplateTypeFilter.DECIDER -> {
                    _decidingSetOptionsFetchResult.value = null
                    val result =
                        setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.DECIDER)
                    _decidingSetOptionsFetchResult.value = result
                }

                SetTemplateTypeFilter.ALL -> {
                    _regularSetOptionsFetchResult.value = null
                    _decidingSetOptionsFetchResult.value = null
                    val result = setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.ALL)
                    _regularSetOptionsFetchResult.value = result
                    _decidingSetOptionsFetchResult.value = result
                }
            }
        }
    }

    private fun fetchParticipants() {
        viewModelScope.launch {
            participantRepository.fetchParticipantsForTournament(tournamentId)
        }
    }

    private fun fetchThemes() {
        viewModelScope.launch {
            _themesFetchResult.value = null
            val result = themeRepository.fetchThemes()
            _themesFetchResult.value = result
        }
    }

    private fun selectParticipant(participantNumber: Int, participant: TennisParticipant) {
        when (participant) {
            is SinglesParticipant -> {
                val displayName = participant.player.surname.uppercase()
                when (participantNumber) {
                    1 -> {
                        val current = _firstParticipant.value as ParticipantInSinglesMatch
                        val player = current.player as PlayerInSinglesMatch
                        _firstParticipant.value = current.copy(
                            id = participant.id,
                            seed = participant.seed,
                            displayName = displayName,
                            player = player.copy(
                                id = participant.player.id,
                                surname = participant.player.surname,
                                name = participant.player.name
                            )
                        )
                    }

                    2 -> {
                        val current = _secondParticipant.value as ParticipantInSinglesMatch
                        val player = current.player as PlayerInSinglesMatch
                        _secondParticipant.value = current.copy(
                            id = participant.id,
                            seed = participant.seed,
                            displayName = displayName,
                            player = player.copy(
                                id = participant.player.id,
                                surname = participant.player.surname,
                                name = participant.player.name
                            )
                        )
                    }
                }
            }

            is DoublesParticipant -> {
                val displayName =
                    "${participant.firstPlayer.surname}/${participant.secondPlayer.surname}".uppercase()
                when (participantNumber) {
                    1 -> {
                        val current = _firstParticipant.value as ParticipantInDoublesMatch
                        val fp = current.firstPlayer as PlayerInDoublesMatch
                        val sp = current.secondPlayer as PlayerInDoublesMatch
                        _firstParticipant.value = current.copy(
                            id = participant.id,
                            seed = participant.seed,
                            displayName = displayName,
                            firstPlayer = fp.copy(
                                id = participant.firstPlayer.id,
                                surname = participant.firstPlayer.surname,
                                name = participant.firstPlayer.name
                            ),
                            secondPlayer = sp.copy(
                                id = participant.secondPlayer.id,
                                surname = participant.secondPlayer.surname,
                                name = participant.secondPlayer.name
                            ),
                        )
                    }

                    2 -> {
                        val current = _secondParticipant.value as ParticipantInDoublesMatch
                        val fp = current.firstPlayer as PlayerInDoublesMatch
                        val sp = current.secondPlayer as PlayerInDoublesMatch
                        _secondParticipant.value = current.copy(
                            id = participant.id,
                            seed = participant.seed,
                            displayName = displayName,
                            firstPlayer = fp.copy(
                                id = participant.firstPlayer.id,
                                surname = participant.firstPlayer.surname,
                                name = participant.firstPlayer.name
                            ),
                            secondPlayer = sp.copy(
                                id = participant.secondPlayer.id,
                                surname = participant.secondPlayer.surname,
                                name = participant.secondPlayer.name
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun addMatch() {
        viewModelScope.launch {
            _isAdding.value = true

            val first = _firstParticipant.value
            val second = _secondParticipant.value

            val firstParticipantBody = ParticipantInMatchBody(
                id = first.id.toString(),
                displayName = first.displayName,
                primaryColor = first.primaryColor.convertToRgbString(),
                secondaryColor = first.secondaryColor?.convertToRgbString()
            )
            val secondParticipantBody = ParticipantInMatchBody(
                id = second.id.toString(),
                displayName = second.displayName,
                primaryColor = second.primaryColor.convertToRgbString(),
                secondaryColor = second.secondaryColor?.convertToRgbString()
            )

            val matchBody = MatchBody(
                firstParticipant = firstParticipantBody,
                secondParticipant = secondParticipantBody,
                setsToWin = _setsToWin.value,
                regularSet = _regularSetId.value?.toString(),
                decidingSet = _decidingSetId.value.toString(),
                themeId = _themeId.value.toString(),
            )

            matchRepository.addNewMatch(tournamentId = tournamentId, matchBody = matchBody)
                .doOnSuccess {
                    _isAdding.value = false
                    sendAction(AddMatchAction.MatchAdded)
                }
                .doOnError {
                    _isAdding.value = false
                    handleError(it)
                }
        }
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is NetworkException ->
                sendAction(AddMatchAction.ShowError(getString(Res.string.check_internet_connection)))
            is UnauthorizedActionException ->
                sendAction(AddMatchAction.ShowUnauthorizedActionError)

            else ->
                sendAction(AddMatchAction.ShowError(e.message ?: "Error"))
        }
    }
}
