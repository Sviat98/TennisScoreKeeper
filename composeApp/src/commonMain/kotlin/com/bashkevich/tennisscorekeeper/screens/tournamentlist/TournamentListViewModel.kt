package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.UnauthorizedException
import com.bashkevich.tennisscorekeeper.core.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TournamentListViewModel(
    private val tournamentRepository: TournamentRepository,
    private val authRepository: AuthRepository,
) :
    BaseViewModel<TournamentListState, TournamentListUiEvent, TournamentListAction>() {

    private val _state = MutableStateFlow(TournamentListState.initial())
    override val state: StateFlow<TournamentListState>
        get() = _state.asStateFlow()

    val actions: Flow<TournamentListAction>
        get() = super.action

    init {
        showTournaments()

        viewModelScope.launch {
            tournamentRepository.observeNewTournament().distinctUntilChanged()
                .collect { newTournament ->
                    val tournaments = state.value.tournaments.toMutableList()

                    tournaments.add(newTournament)
                    reduceState { oldState ->
                        oldState.copy(
                            loadingState = TournamentListLoadingState.Success,
                            tournaments = tournaments.toList()
                        )
                    }
                }
        }
    }

    private fun showTournaments() {
        viewModelScope.launch {
            reduceState { oldState -> oldState.copy(loadingState = TournamentListLoadingState.Loading) }

            val tournamentsResultAsync = async {
                println("START THE tournamentsResultAsync")
                val appConfig = AppConfig.current
                AppConfig.logBuildMode()
                println("appConfig = $appConfig")
                tournamentRepository.getTournaments()
            }

            val refreshTokenStatusResultAsync = async {
                println("START THE refreshTokenStatusResultAsync")
                authRepository.checkRefreshTokenStatus()
            }

            val tournamentsResult = tournamentsResultAsync.await()

            val refreshTokenStatusResult = refreshTokenStatusResultAsync.await()
            if (tournamentsResult is LoadResult.Error || (refreshTokenStatusResult is LoadResult.Error && refreshTokenStatusResult.result !is UnauthorizedException)) {
                reduceState { oldState -> oldState.copy(loadingState = TournamentListLoadingState.Error) }
            } else {
                tournamentsResult.doOnSuccess { tournaments ->
                    reduceState { oldState ->
                        oldState.copy(
                            loadingState = TournamentListLoadingState.Success,
                            tournaments = tournaments
                        )
                    }
                }
            }
        }
    }

    fun onEvent(uiEvent: TournamentListUiEvent) {
        // some feature-specific logic
        when (uiEvent) {
            is TournamentListUiEvent.LoadTournaments -> {
                showTournaments()
            }
        }
    }

    private fun reduceState(reducer: (TournamentListState) -> TournamentListState) {
        _state.update(reducer)
    }

}