package com.bashkevich.tennisscorekeeper.screens.scoreboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardThemeState
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardRoute

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreboardViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val themeRepository: ThemeRepository
) : BaseViewModel<ScoreboardState, ScoreboardUiEvent, ScoreboardAction>() {

    private val matchId = savedStateHandle.toRoute<ScoreboardRoute>().matchId

    // Один сетевой поток на экран (shareIn): и state, и загрузка темы делят одну WS-сессию.
    // Network-only (без кэширования в БД): Scoreboard не должен касаться Room/OPFS —
    // в WebView стриминговых приложений OPFS недоступен и крашит приложение.
    private val matchUpdates = matchRepository.observeMatchUpdatesFromNetwork(matchId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    // --- Тема матча: /themes/{themeId} → оперативная память, без БД ---
    // themeId берём только из успешных результатов: при LoadResult.Error (разрыв сети)
    // идентификатор не меняется, и загруженная тема остаётся в памяти вместо сброса на DEFAULT
    private val themeIdFlow: Flow<Int> = matchUpdates
        .mapNotNull { result ->
            if (result is LoadResult.Success) result.result.themeId else null
        }
        .distinctUntilChanged()

    private val refreshThemeUseCase = ScoreboardRefreshThemeUseCase(themeRepository, themeIdFlow)

    private val themeFetchResult = refreshThemeUseCase.fetchFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val themeLoadState: StateFlow<ScoreboardThemeState> = combine(
        themeIdFlow,
        themeFetchResult
    ) { _, fetchResult ->
        when (fetchResult) {
            is LoadResult.Success -> ScoreboardThemeState.Loaded(fetchResult.result)
            is LoadResult.Error -> ScoreboardThemeState.Error
            else -> ScoreboardThemeState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ScoreboardThemeState.Loading
    )

    override val state: StateFlow<ScoreboardState> = combine(
        matchUpdates,
        matchRepository.observeConnectionState(),
        themeLoadState
    ) { matchResult, connectionState, themeState ->
        val match = when (matchResult) {
            is LoadResult.Success -> matchResult.result
            is LoadResult.Error -> this@ScoreboardViewModel.state.value.match
        }
        ScoreboardState(match = match, connectionState = connectionState, themeState = themeState)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ScoreboardState.initial()
    )

    fun onEvent(uiEvent: ScoreboardUiEvent) {
        when (uiEvent) {
            ScoreboardUiEvent.RetryThemeLoad -> refreshThemeUseCase.refresh()
        }
    }

    override fun onCleared() {
        super.onCleared()
        matchRepository.closeSession()
    }
}
