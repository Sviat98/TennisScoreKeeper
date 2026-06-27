package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute

class MatchDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) :
    BaseViewModel<MatchDetailsState, MatchDetailsUiEvent, MatchDetailsAction>() {
    private val matchId: String = savedStateHandle.toRoute<MatchDetailsRoute>().id

    private val _connectionState = MutableStateFlow(ConnectionState.Loading)

    private val matchUpdates = matchRepository.observeMatchUpdates(matchId)
        .onEach { result ->
            result
                .doOnSuccess { _connectionState.value = ConnectionState.Connected }
                .doOnError { _connectionState.value = ConnectionState.Disconnected }
        }

    override val state: StateFlow<MatchDetailsState> = combine(
        matchUpdates,
        _connectionState,
        _action
    ) { matchResult, connectionState, action ->
        val match = when (matchResult) {
            is LoadResult.Success -> matchResult.result
            is LoadResult.Error -> this@MatchDetailsViewModel.state.value.match
        }
        MatchDetailsState(
            match = match,
            connectionState = connectionState,
            error = (matchResult as? LoadResult.Error)?.result
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

    private fun updateMatchScore(participantId: String, scoreType: ScoreType) {
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

    private fun setFirstParticipantToServe(participantId: String) {
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

    private fun setFirstPlayerInPairToServe(playerId: String) {
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

    private fun setParticipantRetired(participantId: String) {
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

    private fun handleError(e: Throwable){
        if (e is UnauthorizedActionException) {
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
