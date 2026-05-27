package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.model.set_template.domain.EMPTY_REGULAR_SET_TEMPLATE
import com.bashkevich.tennisscorekeeper.model.tournament.remote.AddTournamentBody
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel

class AddTournamentViewModel(
    private val tournamentRepository: TournamentRepository,
    private val setTemplateRepository: SetTemplateRepository,
    private val themeRepository: ThemeRepository,
) : BaseViewModel<AddTournamentState, AddTournamentUiEvent, AddTournamentAction>() {

    private val _state = MutableStateFlow(AddTournamentState.initial())
    override val state: StateFlow<AddTournamentState>
        get() = _state.asStateFlow()

    val actions: Flow<AddTournamentAction>
        get() = super.action

    private var setTemplatesLoaded = false
    private var themesLoaded = false

    fun onEvent(uiEvent: AddTournamentUiEvent) {
        when (uiEvent) {
            is AddTournamentUiEvent.SelectTournamentType -> {
                reduceState { oldState -> oldState.copy(tournamentType = uiEvent.tournamentType) }
            }

            is AddTournamentUiEvent.SelectRegularSetTemplate -> {
                reduceState { oldState -> oldState.copy(regularSetTemplate = uiEvent.setTemplate) }
            }

            is AddTournamentUiEvent.SelectDecidingSetTemplate -> {
                reduceState { oldState -> oldState.copy(decidingSetTemplate = uiEvent.setTemplate) }
            }

            is AddTournamentUiEvent.SelectTheme -> {
                reduceState { oldState -> oldState.copy(selectedTheme = uiEvent.theme) }
            }

            is AddTournamentUiEvent.ChangeSetsToWin -> {
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

            is AddTournamentUiEvent.FetchSetTemplates -> fetchSetTemplates()

            is AddTournamentUiEvent.FetchThemes -> fetchThemes()

            is AddTournamentUiEvent.AddTournament -> addTournament(
                tournamentName = uiEvent.tournamentName,
                tournamentType = uiEvent.tournamentType,
                defaultSetTemplateId = uiEvent.defaultSetTemplateId,
                decidingSetTemplateId = uiEvent.decidingSetTemplateId,
                themeId = uiEvent.themeId,
                setsToWin = uiEvent.setsToWin,
            )
        }
    }

    private fun addTournament(
        tournamentName: String,
        tournamentType: TournamentType,
        defaultSetTemplateId: String,
        decidingSetTemplateId: String,
        themeId: String,
        setsToWin: Int,
    ) {
        viewModelScope.launch {
            reduceState { oldState -> oldState.copy(tournamentAddingSubstate = TournamentAddingSubstate.Loading) }
            val addTournamentBody = AddTournamentBody(
                name = tournamentName,
                type = tournamentType,
                regularSetTemplateId = defaultSetTemplateId,
                decidingSetTemplateId = decidingSetTemplateId,
                themeId = themeId,
                setsToWin = setsToWin,
            )
            val addTournamentResult = tournamentRepository.addTournament(addTournamentBody)

            when (addTournamentResult) {
                is LoadResult.Success -> {
                    val newTournament = addTournamentResult.result
                    tournamentRepository.emitNewTournament(newTournament)
                    reduceState { oldState -> oldState.copy(tournamentAddingSubstate = TournamentAddingSubstate.Success) }
                }

                is LoadResult.Error -> {
                    val errorMessage = addTournamentResult.result.message ?: ""
                    reduceState { oldState ->
                        oldState.copy(
                            tournamentAddingSubstate = TournamentAddingSubstate.Error(
                                message = errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    private fun fetchSetTemplates() {
        if (setTemplatesLoaded) return
        setTemplatesLoaded = true
        viewModelScope.launch {
            launch {
                setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.ALL).collect { templates ->
                    reduceState { oldState ->
                        oldState.copy(
                            setTemplateOptions = templates,
                        )
                    }
                }
            }
            setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.ALL)
        }
    }

    private fun fetchThemes() {
        if (themesLoaded) return
        themesLoaded = true
        viewModelScope.launch {
            launch {
                themeRepository.observeThemesFromDatabase().collect { themes ->
                    reduceState { oldState ->
                        oldState.copy(
                            themeOptions = themes,
                        )
                    }
                }
            }
            themeRepository.fetchThemes()
        }
    }

    private fun reduceState(reducer: (AddTournamentState) -> AddTournamentState) {
        _state.update(reducer)
    }
}
