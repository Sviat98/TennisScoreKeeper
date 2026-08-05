package com.bashkevich.tennisscorekeeper.screens.settings.addtheme

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.toThemeBody
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection
import tennisscorekeeper.shared.generated.resources.theme_save_error

class AddThemeViewModel(
    private val themeRepository: ThemeRepository,
) : BaseViewModel<AddThemeState, AddThemeUiEvent, AddThemeAction>() {

    val themeNameState = TextFieldState()

    private val _editedTheme = MutableStateFlow(ScoreboardTheme.DEFAULT)
    private val _isSaving = MutableStateFlow(false)

    override val state: StateFlow<AddThemeState> = combine(
        _editedTheme,
        _isSaving,
        _action
    ) { editedTheme, isSaving, action ->
        AddThemeState(
            editedTheme = editedTheme,
            isSaving = isSaving,
            action = action
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddThemeState.initial()
    )

    fun onEvent(uiEvent: AddThemeUiEvent) {
        when (uiEvent) {
            is AddThemeUiEvent.UpdateColor -> {
                _editedTheme.value = uiEvent.field.applyTo(_editedTheme.value, uiEvent.color)
            }

            is AddThemeUiEvent.AddTheme -> addTheme()
        }
    }

    private fun addTheme() {
        val name = themeNameState.text.trim().toString()
        if (name.isBlank()) return
        viewModelScope.launch {
            _isSaving.value = true
            themeRepository.createTheme(_editedTheme.value.copy(name = name).toThemeBody())
                .doOnSuccess {
                    _isSaving.value = false
                    sendAction(AddThemeAction.ThemeSaved)
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
                sendAction(AddThemeAction.ShowError(getString(Res.string.check_internet_connection)))

            is UnauthorizedActionException ->
                sendAction(AddThemeAction.ShowUnauthorizedActionError)

            else ->
                sendAction(AddThemeAction.ShowError(e.message ?: getString(Res.string.theme_save_error)))
        }
    }
}
