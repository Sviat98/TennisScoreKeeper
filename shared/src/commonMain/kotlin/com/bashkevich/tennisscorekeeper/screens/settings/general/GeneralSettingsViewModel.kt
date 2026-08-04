package com.bashkevich.tennisscorekeeper.screens.settings.general

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.settings.repository.SettingsRepository
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GeneralSettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<GeneralSettingsState, GeneralSettingsUiEvent, GeneralSettingsAction>() {

    // State is built reactively — NO init{} block (project rule).
    override val state: StateFlow<GeneralSettingsState> =
        combine(
            settingsRepository.observeAppThemeMode(),
            settingsRepository.observeAppLanguage(),
        ) { themeMode, language ->
            GeneralSettingsState(appThemeMode = themeMode, appLanguage = language)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            GeneralSettingsState.initial(),
        )

    fun onEvent(uiEvent: GeneralSettingsUiEvent) {
        when (uiEvent) {
            is GeneralSettingsUiEvent.ChangeThemeMode -> {
                // DEBUG: trace theme selection (web console / logcat). Remove later.
                println("[ThemeSettings] onEvent ChangeThemeMode: mode=${uiEvent.mode}")
                viewModelScope.launch {
                    settingsRepository.saveAppThemeMode(uiEvent.mode)
                }
            }
            is GeneralSettingsUiEvent.ChangeLanguage -> {
                viewModelScope.launch {
                    settingsRepository.saveAppLanguage(uiEvent.language)
                }
            }
        }
    }
}
