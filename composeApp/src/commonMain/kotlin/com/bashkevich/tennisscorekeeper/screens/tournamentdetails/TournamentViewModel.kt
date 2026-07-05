package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.check_internet_connection
import com.bashkevich.tennisscorekeeper.model.file.domain.EMPTY_EXCEL_FILE
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab

class TournamentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository,
    private val matchRepository: MatchRepository,
    private val participantRepository: ParticipantRepository
) : BaseViewModel<TournamentState, TournamentUiEvent, TournamentAction>() {

    private val tournamentId: Int = savedStateHandle.toRoute<TournamentRoute>().tournamentId

    init {
        println("tournamentId = $tournamentId")
    }

    private val refreshTournamentDetails = RefreshTournamentDetailsUseCase(
        tournamentRepository = tournamentRepository,
        matchRepository = matchRepository,
        participantRepository = participantRepository
    )

    private val _isRefreshing = MutableStateFlow(false)
    private val _currentTab = MutableStateFlow(TournamentTab.MATCHES)
    private val _isUploadInProgress = MutableStateFlow(false)
    private val _participantsFile = MutableStateFlow(EMPTY_EXCEL_FILE)
    private val hasTabOpened = TournamentTab.entries.associateWith { false }.toMutableMap()

    private val tournamentDetailsData = combine(
        refreshTournamentDetails.observeTournamentByIdFromNetwork(tournamentId),
        tournamentRepository.observeTournamentById(tournamentId)
    ) { network, db -> network to db }

    private val matchesData = combine(
        refreshTournamentDetails.observeMatchesListFromNetwork(tournamentId),
        matchRepository.observeMatchesForTournament(tournamentId)
    ) { network, db -> network to db }

    private val participantsData = combine(
        refreshTournamentDetails.observeParticipantsFromNetwork(tournamentId),
        participantRepository.observeParticipantsForTournament(tournamentId)
    ) { network, db -> network to db }

    private val _data = combine(
        tournamentDetailsData,
        matchesData,
        participantsData,
        _currentTab
    ) { tournamentDetails, matches, participants, tab ->
        val tabNetworkState = when (tab) {
            TournamentTab.MATCHES -> matches.first
            TournamentTab.PARTICIPANTS -> participants.first
        }

        if (tabNetworkState is LoadResult.Error) {
            val hasData = when (tab) {
                TournamentTab.MATCHES -> matches.second.isNotEmpty()
                TournamentTab.PARTICIPANTS -> participants.second.isNotEmpty()
            }
            if (hasData) {
                handleError(tabNetworkState.result)
            }
        }

        if (tournamentDetails.first != null && tabNetworkState != null) {
            _isRefreshing.value = false
        }

        if (matches.first != null) {
            hasTabOpened[TournamentTab.MATCHES] = true
        }

        if (participants.first != null) {
            hasTabOpened[TournamentTab.PARTICIPANTS] = true
        }

        TournamentDetailsData(tournamentDetails, matches, participants, tab)
    }

    override val state: StateFlow<TournamentState> = combine(
        _data,
        _isRefreshing,
        _action,
        _isUploadInProgress,
        _participantsFile
    ) { data, isRefreshing, action, isUploadInProgress, participantsFile ->
        val (tournamentNetwork, tournament) = data.tournamentDetails
        val (matchesNetwork, matchesList) = data.matches
        val (participantsNetwork, participantsList) = data.participants


        val tournamentDetailsState =
            if (tournamentNetwork == null && tournament == TOURNAMENT_DEFAULT) {
                TournamentDetailsLoadingState.Loading
            } else {
                TournamentDetailsLoadingState.Content(tournament)
            }

        val matchListLoadingState = when {
            hasTabOpened[TournamentTab.MATCHES] == false && matchesNetwork == null && matchesList.isEmpty() ->
                MatchListLoadingState.Loading

            matchesNetwork is LoadResult.Error && matchesList.isEmpty() ->
                MatchListLoadingState.InitialError

            else ->
                MatchListLoadingState.Content(matchesList)
        }

        val participantListLoadingState = when {
            hasTabOpened[TournamentTab.PARTICIPANTS] == false && participantsNetwork == null && participantsList.isEmpty() ->
                ParticipantListLoadingState.Loading

            participantsNetwork is LoadResult.Error && participantsList.isEmpty() ->
                ParticipantListLoadingState.InitialError

            else ->
                ParticipantListLoadingState.Content(
                    participants = participantsList,
                    isUploadInProgress = isUploadInProgress,
                    participantsFile = participantsFile
                )
        }

        TournamentState(
            tournamentDetailsState = tournamentDetailsState,
            matchListLoadingState = matchListLoadingState,
            participantListLoadingState = participantListLoadingState,
            activeTab = data.tab,
            isRefreshing = isRefreshing,
            action = action
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        TournamentState.initial()
    )

    fun onEvent(uiEvent: TournamentUiEvent) {
        when (uiEvent) {
            is TournamentUiEvent.Refresh -> refreshTournamentDetailsScreen()
            is TournamentUiEvent.ChangeTournamentStatus -> changeTournamentStatus(uiEvent.tournamentStatus)
            TournamentUiEvent.UploadFile -> uploadFile()
            is TournamentUiEvent.SelectFile -> {
                _participantsFile.value = uiEvent.file
            }

            is TournamentUiEvent.SwitchTab -> {
                val tab = uiEvent.tab
                _currentTab.value = tab
                if (hasTabOpened[tab] == false) {
                    refreshTournamentDetails.refresh(
                        RefreshTournamentDetailsInfo(
                            tournamentTab = tab,
                            updateTournamentHeader = false
                        )
                    )
                }
            }
        }
    }

    private fun refreshTournamentDetailsScreen() {
        _isRefreshing.value = true
        refreshTournamentDetails.refresh(
            RefreshTournamentDetailsInfo(
                tournamentTab = _currentTab.value,
                updateTournamentHeader = true
            )
        )
    }

    private fun changeTournamentStatus(tournamentStatus: TournamentStatus) {
        viewModelScope.launch {
            tournamentRepository.changeTournamentStatus(tournamentId, tournamentStatus)
        }
    }

    private fun uploadFile() {
        viewModelScope.launch {
            _isUploadInProgress.value = true

            val loadResult = participantRepository.uploadParticipantsFile(
                tournamentId = tournamentId,
                participantsFile = _participantsFile.value
            )

            if (loadResult is LoadResult.Success) {
                _isUploadInProgress.value = false
                _participantsFile.value = EMPTY_EXCEL_FILE
            } else {
                _isUploadInProgress.value = false
            }
        }
    }

    override fun onCleared() {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            runCatching {
                println("onCLeared TRIGGER")
                matchRepository.deleteMatchesForTournament(tournamentId)
                participantRepository.deleteParticipantsForTournament(tournamentId)
            }
        }
        super.onCleared()
    }

    private suspend fun handleError(e: Throwable) {
        val message = if (e is NetworkException)
            getString(Res.string.check_internet_connection)
        else e.message ?: "Error"
        sendAction(TournamentAction.ShowError(message))
    }

    private data class TournamentDetailsData(
        val tournamentDetails: Pair<LoadResult<Unit, Throwable>?, Tournament>,
        val matches: Pair<LoadResult<Unit, Throwable>?, List<ShortMatch>>,
        val participants: Pair<LoadResult<Unit, Throwable>?, List<TennisParticipant>>,
        val tab: TournamentTab
    )
}
