package com.bashkevich.tennisscorekeeper.screens.settings.themelist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection

class ScoreboardThemeListViewModel(
    private val themeRepository: ThemeRepository
) : BaseViewModel<ScoreboardThemeListState, ScoreboardThemeListUiEvent, ScoreboardThemeListAction>() {

    private val _isRefreshing = MutableStateFlow(false)

    private val refreshUseCase = RefreshThemeListUseCase(themeRepository)

    private val _networkAndThemes = combine(
        refreshUseCase.fetchThemesFlow(),
        themeRepository.observeThemesFromDatabase()
    ) { networkState, themes ->
        networkState to themes
    }.onEach { (networkState, themes) ->
        if (networkState is LoadResult.Error && themes.isNotEmpty()) {
            handleError(networkState.result)
        }
        if (networkState != null) {
            _isRefreshing.value = false
        }
    }

    override val state: StateFlow<ScoreboardThemeListState> = combine(
        _networkAndThemes,
        _isRefreshing,
        _action
    ) { (networkState, themes), isRefreshing, action ->
        val loadingState = when {
            !isRefreshing && networkState == null && themes.isEmpty() ->
                ScoreboardThemeListLoadingState.Loading
            networkState is LoadResult.Error && themes.isEmpty() ->
                ScoreboardThemeListLoadingState.Error(networkState.result.message ?: "Error")
            else ->
                ScoreboardThemeListLoadingState.Content(themes, isRefreshing)
        }
        ScoreboardThemeListState(loadingState = loadingState, action = action)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ScoreboardThemeListState.initial()
    )

    fun refresh() {
        _isRefreshing.value = true
        refreshUseCase.refreshThemes()
    }

    fun retry() {
        refreshUseCase.refreshThemes()
    }

    fun onEvent(uiEvent: ScoreboardThemeListUiEvent) {
        when (uiEvent) {
            is ScoreboardThemeListUiEvent.RefreshThemes -> refresh()
            is ScoreboardThemeListUiEvent.Retry -> retry()
        }
    }

    private suspend fun handleError(e: Throwable) {
        val message = if (e is NetworkException)
            getString(Res.string.check_internet_connection)
        else e.message ?: "Error"
        sendAction(ScoreboardThemeListAction.ShowError(message))
    }
}
