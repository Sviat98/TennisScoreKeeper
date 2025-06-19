package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.ParticipantInMatchBody
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_REGULAR_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import kotlinx.coroutines.launch

class AddMatchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val tournamentRepository: TournamentRepository,
    private val participantRepository: ParticipantRepository,
    private val setTemplateRepository: SetTemplateRepository
) : BaseViewModel<AddMatchState, AddMatchUiEvent, AddMatchAction>() {

    private val _state = MutableStateFlow(AddMatchState.initial())
    override val state: StateFlow<AddMatchState>
        get() = _state.asStateFlow()

    val actions: Flow<AddMatchAction>
        get() = super.action

    init {
        fetchTournament()
    }

    fun onEvent(uiEvent: AddMatchUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is AddMatchUiEvent.FetchParticipants -> fetchParticipants()
            is AddMatchUiEvent.FetchSetTemplates -> fetchSetTemplates(uiEvent.setTemplateTypeFilter)
            is AddMatchUiEvent.SelectParticipant -> {
                val participant = uiEvent.participant
                when (uiEvent.participantNumber) {
                    1 -> reduceState { oldState ->
                        oldState.copy(
                            firstParticipant = participant,
                            firstParticipantDisplayName = participant.toDisplayNameState()
                        )
                    }

                    2 -> reduceState { oldState ->
                        oldState.copy(
                            secondParticipant = participant,
                            secondParticipantDisplayName = participant.toDisplayNameState()
                        )
                    }
                }
            }

            is AddMatchUiEvent.ChangeSetsToWin -> {
                val setsToWinNewValue = state.value.setsToWin + uiEvent.delta

                val regularSetTemplate =
                    if (setsToWinNewValue < 2) EMPTY_REGULAR_SET_TEMPLATE else state.value.regularSetTemplate
                reduceState { oldState ->
                    oldState.copy(
                        setsToWin = setsToWinNewValue,
                        regularSetTemplate = regularSetTemplate
                    )
                }
            }

            is AddMatchUiEvent.SelectSetTemplate -> {
                val setTemplate = uiEvent.setTemplate
                when (uiEvent.setTemplateTypeFilter) {
                    SetTemplateTypeFilter.REGULAR -> reduceState { oldState ->
                        oldState.copy(
                            regularSetTemplate = setTemplate,
                        )
                    }

                    SetTemplateTypeFilter.DECIDER -> reduceState { oldState ->
                        oldState.copy(
                            decidingSetTemplate = setTemplate,
                        )
                    }

                    else -> Unit
                }
            }

            is AddMatchUiEvent.AddMatch -> buildAndEmitMatchBody()
        }
    }

    private fun fetchTournament() {
        viewModelScope.launch {
            val tournamentId = savedStateHandle.toRoute<AddMatchRoute>().tournamentId

            val tournamentResult = tournamentRepository.getTournamentById(tournamentId)


            if (tournamentResult is LoadResult.Success) {

                val tournament = tournamentResult.result

                val participantDisplayNameState = when (tournament.type) {
                    TournamentType.SINGLES -> EMPTY_SINGLES_PARTICIPANT_DISPLAY_NAME
                    TournamentType.DOUBLES -> EMPTY_DOUBLES_PARTICIPANT_DISPLAY_NAME
                }

                reduceState { oldState ->
                    oldState.copy(
                        isLoading = false,
                        tournament = tournament,
                        firstParticipantDisplayName = participantDisplayNameState,
                        secondParticipantDisplayName = participantDisplayNameState
                    )
                }
            }
        }
    }

    private fun fetchSetTemplates(setTemplateTypeFilter: SetTemplateTypeFilter) {
        viewModelScope.launch {

            val setTemplatesResult =
                setTemplateRepository.getSetTemplates(setTemplateTypeFilter)

            if (setTemplatesResult is LoadResult.Success) {
                reduceState { oldState ->
                    oldState.copy(
                        setTemplateOptions = setTemplatesResult.result
                    )
                }
            }
        }
    }

    private fun fetchParticipants() {
        viewModelScope.launch {
            val tournamentId = state.value.tournament.id

            if (state.value.participantOptions.isEmpty()) {
                val participantsResult =
                    participantRepository.getParticipantsForTournament(tournamentId)

                if (participantsResult is LoadResult.Success) {
                    reduceState { oldState ->
                        oldState.copy(
                            participantOptions = participantsResult.result
                        )
                    }
                }
            }

        }
    }

    private fun buildAndEmitMatchBody(){
        val state = state.value

        val firstParticipantDisplayName = when (state.firstParticipantDisplayName) {
            is SinglesParticipantDisplayNameState -> state.firstParticipantDisplayName.playerDisplayName.text
            is DoublesParticipantDisplayNameState -> "${state.firstParticipantDisplayName.firstPlayerDisplayName.text}/${state.firstParticipantDisplayName.secondPlayerDisplayName.text}"
        }

        val secondParticipantDisplayName = when (state.secondParticipantDisplayName) {
            is SinglesParticipantDisplayNameState -> state.secondParticipantDisplayName.playerDisplayName.text
            is DoublesParticipantDisplayNameState -> "${state.secondParticipantDisplayName.firstPlayerDisplayName.text}/${state.secondParticipantDisplayName.secondPlayerDisplayName.text}"
        }
        val firstParticipantInMatchBody = ParticipantInMatchBody(
            id = state.firstParticipant.id,
            displayName = firstParticipantDisplayName.toString()
        )
        val secondParticipantInMatchBody = ParticipantInMatchBody(
            id = state.secondParticipant.id,
            displayName = secondParticipantDisplayName.toString()
        )

        val matchBody = MatchBody(
            firstParticipant = firstParticipantInMatchBody,
            secondParticipant = secondParticipantInMatchBody,
            setsToWin = state.setsToWin,
            regularSet = state.regularSetTemplate.id,
            decidingSet = state.decidingSetTemplate.id
        )

        matchRepository.emitNewMatch(matchBody)
    }

    private fun reduceState(reducer: (AddMatchState) -> AddMatchState) {
        _state.update(reducer)
    }

}