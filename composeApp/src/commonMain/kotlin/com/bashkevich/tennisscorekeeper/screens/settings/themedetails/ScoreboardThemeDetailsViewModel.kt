package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.toThemeBody
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardThemeDetailsRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
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

    private val _editedTheme = MutableStateFlow<ScoreboardTheme?>(null)
    private val _isSaving = MutableStateFlow(false)

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private fun refreshTheme() {
        refreshTrigger.tryEmit(Unit)
    }

    private fun fetchThemeByIdFlow(): Flow<LoadResult<Unit, Throwable>?> = flow {
        refreshTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            themeRepository.fetchThemeById(themeId)
                .doOnSuccess { emit(LoadResult.Success(Unit)) }
                .doOnError { emit(LoadResult.Error(it)) }
        }
    }

    private val _networkAndTheme = combine(
        fetchThemeByIdFlow(),
        themeRepository.observeThemeByIdFromDatabase(themeId),
        _editedTheme
    ) { networkState, dbTheme, editedTheme ->
        Triple(networkState, dbTheme, editedTheme)
    }

    override val state: StateFlow<ScoreboardThemeDetailsState> = combine(
        _networkAndTheme,
        _isSaving,
        _action
    ) { (networkState, dbTheme, editedTheme), isSaving, action ->
        val oldTheme = dbTheme
        val currentEdited = editedTheme ?: oldTheme

        val loadingState = when {
            networkState == null && currentEdited.id == 0 ->
                ThemeDetailsLoadingState.Loading
            networkState is LoadResult.Error && currentEdited.id == 0 ->
                ThemeDetailsLoadingState.Error(networkState.result.message ?: "Error")
            else ->
                ThemeDetailsLoadingState.Content(
                    oldTheme = oldTheme,
                    editedTheme = currentEdited,
                    isSaving = isSaving,
                    hasChanges = oldTheme != currentEdited
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
            is ThemeDetailsUiEvent.Refresh -> refreshTheme()
            is ThemeDetailsUiEvent.Retry -> refreshTheme()
            is ThemeDetailsUiEvent.UpdateName -> {
                _editedTheme.value = _editedTheme.value?.copy(name = uiEvent.name)
            }
            is ThemeDetailsUiEvent.UpdateColor -> {
                _editedTheme.value = uiEvent.field.applyTo(_editedTheme.value ?: return, uiEvent.color)
            }
            is ThemeDetailsUiEvent.SaveTheme -> saveTheme()
        }
    }

    private fun saveTheme() {
        val edited = _editedTheme.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            themeRepository.updateTheme(themeId, edited.toThemeBody())
                .doOnSuccess {
                    sendAction(ThemeDetailsAction.ThemeSaved)
                }
                .doOnError { e ->
                    val message = if (e is NetworkException)
                        getString(Res.string.check_internet_connection)
                    else e.message ?: "Error"
                    sendAction(ThemeDetailsAction.ShowError(message))
                }
            _isSaving.value = false
        }
    }
}
