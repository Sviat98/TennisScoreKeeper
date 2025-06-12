package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import kotlinx.coroutines.launch

class ParticipantListViewModel(
    savedStateHandle: SavedStateHandle,
    private val participantRepository: ParticipantRepository
    ) :
    BaseViewModel<ParticipantListState, ParticipantListUiEvent, ParticipantListAction>() {

    private val _state = MutableStateFlow(ParticipantListState.initial())
    override val state: StateFlow<ParticipantListState>
        get() = _state.asStateFlow()

    val actions: Flow<ParticipantListAction>
        get() = super.action

    init {
        val tournamentId = savedStateHandle.toRoute<TournamentRoute>().tournamentId

        viewModelScope.launch {
            val loadResult = participantRepository.getParticipantsForTournament(tournamentId)

            println(loadResult)

            if (loadResult is LoadResult.Success){
                onEvent(ParticipantListUiEvent.ShowParticipants(participants = loadResult.result))
            }
        }
    }

    fun onEvent(uiEvent: ParticipantListUiEvent) {
        // some feature-specific logic
        when(uiEvent){
            is ParticipantListUiEvent.ShowParticipants ->{
                reduceState { oldState -> oldState.copy(participants = uiEvent.participants) }
            }
        }

    }

    private fun reduceState(reducer: (ParticipantListState) -> ParticipantListState) {
        _state.update(reducer)
    }

}