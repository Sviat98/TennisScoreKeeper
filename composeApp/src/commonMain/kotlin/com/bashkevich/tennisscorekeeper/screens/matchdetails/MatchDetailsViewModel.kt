package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MatchDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) :
    BaseViewModel<MatchDetailsState, MatchDetailsUiEvent, MatchDetailsAction>() {

    private val _state = MutableStateFlow(MatchDetailsState.initial())
    override val state: StateFlow<MatchDetailsState>
        get() = _state.asStateFlow()

    val actions: Flow<MatchDetailsAction>
        get() = super.action

    init {
        val matchId = savedStateHandle.toRoute<MatchDetailsRoute>().id

        matchRepository.connectToMatchUpdates(matchId = matchId)

        viewModelScope.launch {
            matchRepository.observeMatchUpdates().distinctUntilChanged().collect { result ->
                println(result)
                when (result) {
                    is LoadResult.Success -> {
                        reduceState { oldState -> oldState.copy(match = result.result) }
                    }

                    is LoadResult.Error -> {
                        println(result.result.message)
                    }
                }
            }
        }
    }

    fun onEvent(uiEvent: MatchDetailsUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is MatchDetailsUiEvent.ShowMatch -> {
            }

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
            is MatchDetailsUiEvent.SetParticipantRetired-> setParticipantRetired(
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
            val state = state.value

            val matchId = state.match.id
            matchRepository.updateMatchScore(
                matchId = matchId,
                participantId = participantId,
                scoreType = scoreType
            )
        }
    }

    private fun setFirstParticipantToServe(participantId: String) {
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.setFirstParticipantToServe(
                matchId = matchId,
                participantId = participantId,
            )
        }
    }

    private fun setFirstPlayerInPairToServe(playerId: String) {
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.setFirstPlayerInPairToServe(
                matchId = matchId,
                playerId = playerId,
            )
        }
    }

    private fun setParticipantRetired(participantId: String) {
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.setParticipantRetired(
                matchId = matchId,
                participantId = participantId,
            )
        }    }

    private fun changeMatchStatus(status: MatchStatus) {
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.setMatchStatus(
                matchId = matchId,
                status = status,
            )
        }
    }

    private fun undoPoint(){
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.undoPoint(
                matchId = matchId,
            )
        }
    }

    private fun redoPoint(){
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.redoPoint(
                matchId = matchId,
            )
        }
    }
    private fun attachVideoLink(videoLink: String) {
        viewModelScope.launch {
            val state = state.value

            val matchId = state.match.id
            matchRepository.attachVideoLink(
                matchId = matchId,
                videoLink = videoLink
            )
        }
    }

    private fun reduceState(reducer: (MatchDetailsState) -> MatchDetailsState) {
        _state.update(reducer)
    }

}