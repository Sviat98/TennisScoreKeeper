package com.bashkevich.tennisscorekeeper.screens.participantlist

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel

class ParticipantListViewModel :
    BaseViewModel<ParticipantListState, ParticipantListUiEvent, ParticipantListAction>() {

    private val _state = MutableStateFlow(ParticipantListState.initial())
    override val state: StateFlow<ParticipantListState>
        get() = _state.asStateFlow()

    val actions: Flow<ParticipantListAction>
        get() = super.action

    fun onEvent(uiEvent: ParticipantListUiEvent) {
        // some feature-specific logic
    }

    private fun reduceState(reducer: (ParticipantListState) -> ParticipantListState) {
        _state.update(reducer)
    }

}