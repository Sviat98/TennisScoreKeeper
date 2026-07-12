package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.toThemeBody
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardThemeDetailsRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.check_internet_connection

class ScoreboardThemeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository
) : BaseViewModel<ScoreboardThemeDetailsState, ThemeDetailsUiEvent, ThemeDetailsAction>() {

    private val themeId: Int = savedStateHandle.toRoute<ScoreboardThemeDetailsRoute>().themeId
    val themeNameState = TextFieldState()

    private val _editedTheme = MutableStateFlow(ScoreboardTheme.DEFAULT)
    private val _isSaving = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)

    private val refreshUseCase = RefreshThemeDetailsUseCase(themeRepository, themeId)

    private val _networkAndTheme = combine(
        refreshUseCase.fetchThemeByIdFlow(),
        themeRepository.observeThemeByIdFromDatabase(themeId)
            .filter { it != ScoreboardTheme.DEFAULT },
    ) { networkState, dbTheme ->
        networkState to dbTheme
    }.onEach { (networkState, dbTheme) ->
        if (_editedTheme.value == ScoreboardTheme.DEFAULT) {
            themeNameState.edit { replace(0, length, dbTheme.name) }
            _editedTheme.value = dbTheme
        }
        if (networkState != null) {
            _isRefreshing.value = false
            networkState.doOnError {
                handleError(it)
            }
        }
    }

    override val state: StateFlow<ScoreboardThemeDetailsState> = combine(
        _networkAndTheme,
        _editedTheme,
        _isSaving,
        _isRefreshing,
        _action
    ) { (networkState, dbTheme), editedTheme, isSaving, isRefreshing, action ->
        val oldTheme = dbTheme

        val loadingState = when {
            networkState == null && editedTheme == ScoreboardTheme.DEFAULT ->
                ThemeDetailsLoadingState.Loading

            networkState is LoadResult.Error && editedTheme == ScoreboardTheme.DEFAULT ->
                ThemeDetailsLoadingState.Error(networkState.result.message ?: "Error")

            else ->
                ThemeDetailsLoadingState.Content(
                    oldTheme = oldTheme,
                    editedTheme = editedTheme,
                    isSaving = isSaving,
                    isRefreshing = isRefreshing
                )
        }

        ScoreboardThemeDetailsState(loadingState = loadingState, action = action)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ScoreboardThemeDetailsState.initial()
    )

    fun onEvent(uiEvent: ThemeDetailsUiEvent) {
        when (uiEvent) {
            is ThemeDetailsUiEvent.Refresh -> {
                _isRefreshing.value = true
                refreshUseCase.refreshTheme()
            }

            is ThemeDetailsUiEvent.Retry -> refreshUseCase.refreshTheme()
            is ThemeDetailsUiEvent.UpdateColor -> {
                _editedTheme.value = uiEvent.field.applyTo(_editedTheme.value, uiEvent.color)
            }

            is ThemeDetailsUiEvent.SaveTheme -> saveTheme()
        }
    }

    private fun saveTheme() {
        val name = themeNameState.text.trim().toString()
        viewModelScope.launch {
            _isSaving.value = true
            themeRepository.updateTheme(themeId, _editedTheme.value.copy(name = name).toThemeBody())
                .doOnSuccess {
                    _isSaving.value = false
                    sendAction(ThemeDetailsAction.ThemeSaved)
                }
                .doOnError {
                    _isSaving.value = false
                    handleError(it)
                }
        }
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is NetworkException ->
                sendAction(ThemeDetailsAction.ShowError(getString(Res.string.check_internet_connection)))

            is UnauthorizedActionException ->
                sendAction(ThemeDetailsAction.ShowUnauthorizedActionError)

            else ->
                sendAction(ThemeDetailsAction.ShowError(e.message ?: "Не удалось сохранить тему"))
        }
    }
}
