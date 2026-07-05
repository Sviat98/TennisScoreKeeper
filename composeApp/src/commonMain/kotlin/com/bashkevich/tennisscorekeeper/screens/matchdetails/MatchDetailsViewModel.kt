package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.check_internet_connection
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute

class MatchDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val themeRepository: ThemeRepository
) :
    BaseViewModel<MatchDetailsState, MatchDetailsUiEvent, MatchDetailsAction>() {
    private val matchId: Int = savedStateHandle.toRoute<MatchDetailsRoute>().id

    init {
        viewModelScope.launch {
            themeRepository.observeThemeByIdFromDatabase(1).collect {
                println(it)
            }
        }
    }

    private val networkUpdates = matchRepository.observeMatchUpdatesFromNetwork(matchId)

    override val state: StateFlow<MatchDetailsState> = combine(
        networkUpdates,
        matchRepository.observeMatchById(matchId),
        matchRepository.observeConnectionState(),
        _action
    ) { _, match, connectionState, action ->
        MatchDetailsState(
            match = match ?: SAMPLE_MATCH,
            connectionState = connectionState,
            action = action
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MatchDetailsState.initial()
    )

    fun onEvent(uiEvent: MatchDetailsUiEvent) {
        when (uiEvent) {
            is MatchDetailsUiEvent.UpdateScore -> updateMatchScore(
                participantId = uiEvent.participantId,
                scoreType = uiEvent.scoreType
            )

            is MatchDetailsUiEvent.SetFirstParticipantToServe -> setFirstParticipantToServe(
                participantId = uiEvent.participantId
            )

            is MatchDetailsUiEvent.SetFirstPlayerInPairToServe -> setFirstPlayerInPairToServe(
                playerId = uiEvent.playerId
            )
            is MatchDetailsUiEvent.SetParticipantRetired -> setParticipantRetired(
                participantId = uiEvent.participantId
            )
            is MatchDetailsUiEvent.ChangeMatchStatus -> changeMatchStatus(status = uiEvent.status)

            is MatchDetailsUiEvent.UndoPoint -> undoPoint()

            is MatchDetailsUiEvent.RedoPoint -> redoPoint()
            is MatchDetailsUiEvent.AttachVideoLink -> attachVideoLink(videoLink = uiEvent.videoLink)
        }
    }

    private fun updateMatchScore(participantId: Int, scoreType: ScoreType) {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.updateMatchScore(
                matchId = matchId,
                participantId = participantId,
                scoreType = scoreType
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun setFirstParticipantToServe(participantId: Int) {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.setFirstParticipantToServe(
                matchId = matchId,
                participantId = participantId,
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun setFirstPlayerInPairToServe(playerId: Int) {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.setFirstPlayerInPairToServe(
                matchId = matchId,
                playerId = playerId,
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun setParticipantRetired(participantId: Int) {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.setParticipantRetired(
                matchId = matchId,
                participantId = participantId,
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun changeMatchStatus(status: MatchStatus) {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.setMatchStatus(
                matchId = matchId,
                status = status,
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun undoPoint() {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.undoPoint(
                matchId = matchId,
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun redoPoint() {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.redoPoint(
                matchId = matchId,
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private fun attachVideoLink(videoLink: String) {
        viewModelScope.launch {
            val matchId = state.value.match.id
            matchRepository.attachVideoLink(
                matchId = matchId,
                videoLink = videoLink
            ).doOnError { error ->
                handleError(error)
            }
        }
    }

    private suspend fun handleError(e: Throwable){
        println("e = $e")
        when (e) {
            is NetworkException ->
                sendAction(MatchDetailsAction.ShowError(getString(Res.string.check_internet_connection)))
            is UnauthorizedActionException ->
                sendAction(MatchDetailsAction.ShowUnauthorizedError)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            matchRepository.closeSession()
        }
    }
}
