package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.file.domain.EMPTY_EXCEL_FILE
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TournamentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository,
    private val matchRepository: MatchRepository,
    private val participantRepository: ParticipantRepository
) : BaseViewModel<TournamentState, TournamentUiEvent, TournamentAction>() {

    private val _state = MutableStateFlow(TournamentState.initial())
    override val state: StateFlow<TournamentState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentAction>
        get() = super.action

    init {
        viewModelScope.launch {
            showTournament()

            val tournamentId = state.value.tournament.id

            coroutineScope {
                launch {
                    val loadResult = matchRepository.getMatchesForTournament(tournamentId)

                    println("matches loadResult = $loadResult")
                    if (loadResult is LoadResult.Success) {
                        reduceState { oldState ->
                            oldState.copy(
                                matchListState = oldState.matchListState.copy(
                                    matches = loadResult.result
                                )
                            )
                        }
                    }
                }

                launch {
                    matchRepository.observeNewMatch().distinctUntilChanged().collect { newMatch ->

                        println("matchBody = $newMatch")

                        val matches = state.value.matchListState.matches.toMutableList()

                        matches.add(newMatch)
                        reduceState { oldState ->
                            oldState.copy(
                                matchListState = oldState.matchListState.copy(
                                    matches = matches.toList()
                                )
                            )
                        }
                    }
                }

                launch {
                    val loadResult =
                        participantRepository.getParticipantsForTournament(tournamentId)

                    println("participants loadResult = $loadResult")
                    if (loadResult is LoadResult.Success) {
                        reduceState { oldState ->
                            oldState.copy(
                                participantListState = oldState.participantListState.copy(
                                    participants = loadResult.result
                                )
                            )
                        }
                    }
                }

                reduceState { oldState -> oldState.copy(isLoading = false) }
            }

        }
    }


    fun onEvent(uiEvent: TournamentUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is TournamentUiEvent.ChangeTournamentStatus -> changeTournamentStatus(uiEvent.tournamentStatus)
            TournamentUiEvent.UploadFile -> uploadFile()
            is TournamentUiEvent.SelectFile -> {
                reduceState { oldState ->
                    oldState.copy(
                        participantListState = oldState.participantListState.copy(
                            participantsFile = uiEvent.file
                        )
                    )
                }
            }
        }
    }

    private suspend fun showTournament() {
        val tournamentId = savedStateHandle.toRoute<AddMatchRoute>().tournamentId

        val tournamentResult = tournamentRepository.getTournamentById(tournamentId)

        if (tournamentResult is LoadResult.Success) {

            val tournament = tournamentResult.result

            reduceState { oldState ->
                oldState.copy(
                    tournament = tournament,
                )
            }
        }
    }

    private fun changeTournamentStatus(tournamentStatus: TournamentStatus) {
        viewModelScope.launch {

            val tournament = state.value.tournament

            val tournamentChangeStatusResult =
                tournamentRepository.changeTournamentStatus(tournament.id, tournamentStatus)

            if (tournamentChangeStatusResult is LoadResult.Success) {

                reduceState { oldState ->
                    oldState.copy(
                        tournament = oldState.tournament.copy(status = tournamentStatus)
                    )
                }
            }
        }
    }

    private fun uploadFile() {
        viewModelScope.launch {
            val state = state.value

            reduceState { oldState ->
                oldState.copy(
                    participantListState = oldState.participantListState.copy(
                        isUploadInProgress = true
                    )
                )
            }

            val excelFile = state.participantListState.participantsFile

            val tournamentId = state.tournament.id

            val loadResult = participantRepository.uploadParticipantsFile(
                tournamentId = tournamentId,
                participantsFile = excelFile
            )

            println("participants loadResult after upload = $loadResult")

            if (loadResult is LoadResult.Success) {
                reduceState { oldState ->
                    oldState.copy(
                        participantListState = oldState.participantListState.copy(
                            isUploadInProgress = false,
                            participants = loadResult.result,
                            participantsFile = EMPTY_EXCEL_FILE
                        )
                    )
                }
            }
        }
    }

    private fun reduceState(reducer: (TournamentState) -> TournamentState) {
        _state.update(reducer)
    }

}