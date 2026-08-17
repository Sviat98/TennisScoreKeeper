package com.bashkevich.tennisscorekeeper.screens.settings.generatetheme

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.core.combine
import com.bashkevich.tennisscorekeeper.core.remote.NetworkException
import com.bashkevich.tennisscorekeeper.core.remote.UnauthorizedActionException
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.file.domain.EMPTY_IMAGE_FILE
import com.bashkevich.tennisscorekeeper.model.file.domain.ImageFile
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.toScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.toThemeBody
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeContent
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.check_internet_connection
import tennisscorekeeper.shared.generated.resources.theme_generate_error
import tennisscorekeeper.shared.generated.resources.theme_image_format_error
import tennisscorekeeper.shared.generated.resources.theme_save_error

class GenerateThemeViewModel(
    private val themeRepository: ThemeRepository,
) : BaseViewModel<GenerateThemeState, GenerateThemeUiEvent, GenerateThemeAction>() {

    val themeNameState = TextFieldState()

    private val _selectedImage = MutableStateFlow(EMPTY_IMAGE_FILE)
    // Ответ ИИ-агента в оригинальном виде
    private val _originalContent = MutableStateFlow<ThemeContent?>(null)
    // Тема с пользовательскими правками цветов
    private val _editedTheme = MutableStateFlow<ScoreboardTheme?>(null)
    private val _isGenerating = MutableStateFlow(false)
    private val _isSaving = MutableStateFlow(false)

    override val state: StateFlow<GenerateThemeState> = combine(
        _selectedImage,
        _originalContent,
        _editedTheme,
        _isGenerating,
        _isSaving,
        _action
    ) { image, originalContent, editedTheme, isGenerating, isSaving, action ->
        GenerateThemeState(
            selectedImageName = image.name,
            imageFile = image,
            originalTheme = originalContent?.toScoreboardTheme(),
            editedTheme = editedTheme,
            isGenerating = isGenerating,
            isSaving = isSaving,
            action = action
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GenerateThemeState.initial()
    )

    fun onEvent(uiEvent: GenerateThemeUiEvent) {
        when (uiEvent) {
            is GenerateThemeUiEvent.SelectImage -> {
                if (!isSupportedImage(uiEvent.image.name)) {
                    viewModelScope.launch {
                        sendAction(
                            GenerateThemeAction.ShowError(getString(Res.string.theme_image_format_error))
                        )
                    }
                    return
                }
                _selectedImage.value = uiEvent.image
                _originalContent.value = null
                _editedTheme.value = null
            }

            is GenerateThemeUiEvent.ClearImage -> {
                _selectedImage.value = EMPTY_IMAGE_FILE
                _originalContent.value = null
                _editedTheme.value = null
            }

            is GenerateThemeUiEvent.Generate -> generate()

            is GenerateThemeUiEvent.UpdateColor -> {
                val current = _editedTheme.value ?: return
                _editedTheme.value = uiEvent.field.applyTo(current, uiEvent.color)
            }

            is GenerateThemeUiEvent.ResetTheme -> {
                _originalContent.value?.toScoreboardTheme()?.let { _editedTheme.value = it }
            }

            is GenerateThemeUiEvent.AddTheme -> addTheme()
        }
    }

    private fun generate() {
        val image = _selectedImage.value
        if (image.name.isBlank()) return
        viewModelScope.launch {
            _isGenerating.value = true
            themeRepository.generateThemeFromImage(image)
                .doOnSuccess { content ->
                    _originalContent.value = content
                    _editedTheme.value = content.toScoreboardTheme()
                    _isGenerating.value = false
                }
                .doOnError {
                    _isGenerating.value = false
                    handleError(it, getString(Res.string.theme_generate_error))
                }
        }
    }

    private fun addTheme() {
        val editedTheme = _editedTheme.value ?: return
        val name = themeNameState.text.trim().toString()
        if (name.isBlank()) return
        viewModelScope.launch {
            _isSaving.value = true
            val themeBody = editedTheme.copy(name = name).toThemeBody()
            themeRepository.createTheme(themeBody)
                .doOnSuccess {
                    _isSaving.value = false
                    sendAction(GenerateThemeAction.ThemeSaved)
                }
                .doOnError {
                    _isSaving.value = false
                    handleError(it, getString(Res.string.theme_save_error))
                }
        }
    }

    private fun isSupportedImage(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }

    private suspend fun handleError(e: Throwable, defaultMessage: String) {
        when (e) {
            is NetworkException ->
                sendAction(GenerateThemeAction.ShowError(getString(Res.string.check_internet_connection)))

            is UnauthorizedActionException ->
                sendAction(GenerateThemeAction.ShowUnauthorizedActionError)

            else ->
                sendAction(GenerateThemeAction.ShowError(e.message ?: defaultMessage))
        }
    }
}
