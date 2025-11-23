package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.ParticipantInMatchBody
import com.bashkevich.tennisscorekeeper.model.match.remote.convertToRgbString
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.PARTICIPANT_IN_DOUBLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.SinglesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInSinglesMatch
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
            is AddMatchUiEvent.SelectParticipant -> selectParticipant(
                participantNumber = uiEvent.participantNumber,
                participant = uiEvent.participant
            )
            is AddMatchUiEvent.ChangeDisplayName->changeDisplayName(participantNumber = uiEvent.participantNumber, displayName = uiEvent.displayName)
            is AddMatchUiEvent.SelectPrimaryColor -> selectPrimaryColor(
                participantNumber = uiEvent.participantNumber,
                color = uiEvent.color
            )

            is AddMatchUiEvent.SelectSecondaryColor -> selectSecondaryColor(
                participantNumber = uiEvent.participantNumber,
                color = uiEvent.color
            )

            is AddMatchUiEvent.OpenColorPickerDialog -> {
                reduceState { oldState ->
                    oldState.copy(
                        dialogState = OpenColorPickerDialogState.OpenColorPicker(
                            participantNumber = uiEvent.participantNumber,
                            colorNumber = uiEvent.colorNumber
                        )
                    )
                }
            }

            AddMatchUiEvent.CloseColorPickerDialog -> {
                reduceState { oldState -> oldState.copy(dialogState = OpenColorPickerDialogState.None) }
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

    private fun changeDisplayName(participantNumber: Int, displayName: String) {
        val state = state.value

        when (participantNumber) {
            1 -> {
                val firstParticipant = state.firstParticipant
                when (firstParticipant) {
                    is ParticipantInSinglesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = firstParticipant.copy(
                                    displayName = displayName
                                )
                            )
                        }
                    }

                    is ParticipantInDoublesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = firstParticipant.copy(
                                    displayName = displayName
                                )
                            )
                        }
                    }
                }
            }

            2 -> {
                val secondParticipant = state.secondParticipant
                when (secondParticipant) {
                    is ParticipantInSinglesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = secondParticipant.copy(
                                    displayName = displayName
                                )
                            )
                        }
                    }

                    is ParticipantInDoublesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = secondParticipant.copy(
                                    displayName = displayName
                                )
                            )
                        }
                    }
                }
            }
        }

        reduceState { oldState -> oldState.copy(dialogState = OpenColorPickerDialogState.None) }
    }

    private fun selectPrimaryColor(
        participantNumber: Int,
        color: Color
    ) {
        val state = state.value

        when (participantNumber) {
            1 -> {
                val firstParticipant = state.firstParticipant
                when (firstParticipant) {
                    is ParticipantInSinglesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = firstParticipant.copy(
                                    primaryColor = color
                                )
                            )
                        }
                    }

                    is ParticipantInDoublesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = firstParticipant.copy(
                                    primaryColor = color
                                )
                            )
                        }
                    }
                }
            }

            2 -> {
                val secondParticipant = state.secondParticipant
                when (secondParticipant) {
                    is ParticipantInSinglesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = secondParticipant.copy(
                                    primaryColor = color
                                )
                            )
                        }
                    }

                    is ParticipantInDoublesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = secondParticipant.copy(
                                    primaryColor = color
                                )
                            )
                        }
                    }
                }
            }
        }

        reduceState { oldState -> oldState.copy(dialogState = OpenColorPickerDialogState.None) }
    }

    private fun selectSecondaryColor(
        participantNumber: Int,
        color: Color?
    ) {
        val state = state.value

        when (participantNumber) {
            1 -> {
                val firstParticipant = state.firstParticipant
                when (firstParticipant) {
                    is ParticipantInSinglesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = firstParticipant.copy(
                                    secondaryColor = color
                                )
                            )
                        }
                    }

                    is ParticipantInDoublesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = firstParticipant.copy(
                                    secondaryColor = color
                                )
                            )
                        }
                    }
                }
            }

            2 -> {
                val secondParticipant = state.secondParticipant
                when (secondParticipant) {
                    is ParticipantInSinglesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = secondParticipant.copy(
                                    secondaryColor = color
                                )
                            )
                        }
                    }

                    is ParticipantInDoublesMatch -> {
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = secondParticipant.copy(
                                    secondaryColor = color
                                )
                            )
                        }
                    }
                }
            }
        }
        reduceState { oldState -> oldState.copy(dialogState = OpenColorPickerDialogState.None) }

    }

    private fun fetchTournament() {
        viewModelScope.launch {
            val tournamentId = savedStateHandle.toRoute<AddMatchRoute>().tournamentId

            val tournamentResult = tournamentRepository.getTournamentById(tournamentId)

            if (tournamentResult is LoadResult.Success) {

                val tournament = tournamentResult.result

                val defaultParticipant = when(tournament.type){
                    TournamentType.SINGLES -> PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
                    TournamentType.DOUBLES -> PARTICIPANT_IN_DOUBLES_MATCH_DEFAULT
                }

                reduceState { oldState ->
                    oldState.copy(
                        isLoading = false,
                        tournament = tournament,
                        firstParticipant = defaultParticipant,
                        secondParticipant = defaultParticipant
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

    private fun selectParticipant(participantNumber: Int, participant: TennisParticipant) {
        val state = state.value
        when (participant) {
            is SinglesParticipant -> {
                val displayName = participant.player.surname.uppercase()

                when (participantNumber) {
                    1 -> {
                        val participantToUpdate =
                            state.firstParticipant as ParticipantInSinglesMatch

                        val playerToUpdate = participantToUpdate.player as PlayerInSinglesMatch
                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = participantToUpdate.copy(
                                    id = participant.id,
                                    seed = participant.seed,
                                    displayName = displayName,
                                    player = playerToUpdate.copy(
                                        id = participant.player.id,
                                        surname = participant.player.surname,
                                        name = participant.player.name
                                    )
                                ),
                            )
                        }
                    }

                    2 -> {
                        val participantToUpdate =
                            state.secondParticipant as ParticipantInSinglesMatch

                        val playerToUpdate = participantToUpdate.player as PlayerInSinglesMatch
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = participantToUpdate.copy(
                                    id = participant.id,
                                    seed = participant.seed,
                                    displayName = displayName,
                                    player = playerToUpdate.copy(
                                        id = participant.player.id,
                                        surname = participant.player.surname,
                                        name = participant.player.name
                                    )
                                ),
                            )
                        }
                    }
                }
            }

            is DoublesParticipant -> {

                val participantDisplayName =
                    "${participant.firstPlayer.surname}/${participant.secondPlayer.surname}".uppercase()

                when (participantNumber) {
                    1 -> {
                        val participantToUpdate =
                            state.firstParticipant as ParticipantInDoublesMatch

                        val firstPlayerToUpdate =
                            participantToUpdate.firstPlayer as PlayerInDoublesMatch
                        val secondPlayerToUpdate =
                            participantToUpdate.secondPlayer as PlayerInDoublesMatch

                        reduceState { oldState ->
                            oldState.copy(
                                firstParticipant = participantToUpdate.copy(
                                    id = participant.id,
                                    seed = participant.seed,
                                    displayName = participantDisplayName,
                                    firstPlayer = firstPlayerToUpdate.copy(
                                        id = participant.firstPlayer.id,
                                        surname = participant.firstPlayer.surname,
                                        name = participant.firstPlayer.name
                                    ),
                                    secondPlayer = secondPlayerToUpdate.copy(
                                        id = participant.secondPlayer.id,
                                        surname = participant.secondPlayer.surname,
                                        name = participant.secondPlayer.name
                                    ),
                                ),
                            )
                        }
                    }

                    2 -> {
                        val participantToUpdate =
                            state.secondParticipant as ParticipantInDoublesMatch

                        val firstPlayerToUpdate =
                            participantToUpdate.firstPlayer as PlayerInDoublesMatch
                        val secondPlayerToUpdate =
                            participantToUpdate.secondPlayer as PlayerInDoublesMatch
                        reduceState { oldState ->
                            oldState.copy(
                                secondParticipant = participantToUpdate.copy(
                                    id = participant.id,
                                    seed = participant.seed,
                                    displayName = participantDisplayName,
                                    firstPlayer = firstPlayerToUpdate.copy(
                                        id = participant.firstPlayer.id,
                                        surname = participant.firstPlayer.surname,
                                        name = participant.firstPlayer.name
                                    ),
                                    secondPlayer = secondPlayerToUpdate.copy(
                                        id = participant.secondPlayer.id,
                                        surname = participant.secondPlayer.surname,
                                        name = participant.secondPlayer.name
                                    ),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun buildAndEmitMatchBody() {
        viewModelScope.launch {
            reduceState { oldState -> oldState.copy(matchAddingSubstate = MatchAddingSubstate.Loading) }
            val state = state.value

            val tournamentId = state.tournament.id

            val firstParticipantInMatchBody = ParticipantInMatchBody(
                id = state.firstParticipant.id,
                displayName = state.firstParticipant.displayName,
                primaryColor = state.firstParticipant.primaryColor.convertToRgbString(),
                secondaryColor = state.firstParticipant.secondaryColor?.convertToRgbString()
            )
            val secondParticipantInMatchBody = ParticipantInMatchBody(
                id = state.secondParticipant.id,
                displayName = state.secondParticipant.displayName,
                primaryColor = state.secondParticipant.primaryColor.convertToRgbString(),
                secondaryColor = state.secondParticipant.secondaryColor?.convertToRgbString()
            )

            val regularSetTemplate = state.regularSetTemplate

            val regularSetTemplateId =
                if (regularSetTemplate == EMPTY_REGULAR_SET_TEMPLATE) null else regularSetTemplate.id

            val matchBody = MatchBody(
                firstParticipant = firstParticipantInMatchBody,
                secondParticipant = secondParticipantInMatchBody,
                setsToWin = state.setsToWin,
                regularSet = regularSetTemplateId,
                decidingSet = state.decidingSetTemplate.id
            )

            val addMatchResult = matchRepository.addNewMatch(tournamentId = tournamentId, matchBody = matchBody)

            when(addMatchResult){
                is LoadResult.Success ->{
                    val newMatch = addMatchResult.result

                    matchRepository.emitNewMatch(newMatch)
                    reduceState { oldState -> oldState.copy(matchAddingSubstate = MatchAddingSubstate.Success) }
                }
                is LoadResult.Error ->{
                    val errorMessage = addMatchResult.result.message ?: ""

                    reduceState { oldState -> oldState.copy(matchAddingSubstate = MatchAddingSubstate.Error(message = errorMessage)) }
                }
            }
        }
    }

    private fun reduceState(reducer: (AddMatchState) -> AddMatchState) {
        _state.update(reducer)
    }

}