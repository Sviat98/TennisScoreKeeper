package com.bashkevich.tennisscorekeeper

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.model.auth.repository.AuthRepository
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import com.bashkevich.tennisscorekeeper.model.settings.repository.SettingsRepository
import com.bashkevich.tennisscorekeeper.mvi.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    // State is built reactively — NO init{} block (project rule).
    // checkRefreshTokenStatus() runs as a one-shot startup side effect via onStart.
    val state: StateFlow<AppState> =
        combine(
            authRepository.observePlayerId().distinctUntilChanged(),
            settingsRepository.observeAppThemeMode().distinctUntilChanged(),
        ) { playerId, themeMode ->
            println("collected themeMode = $themeMode")
            AppState(
                isAuthorized = playerId.isNotEmpty(),
                appThemeMode = themeMode,
            )
        }
            .onStart { authRepository.checkRefreshTokenStatus() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppState.initial())
}

@Immutable
data class AppState(
    val isAuthorized: Boolean,
    val appThemeMode: AppThemeMode,
) : UiState {
    companion object {
        fun initial() = AppState(
            isAuthorized = false,
            appThemeMode = AppThemeMode.SYSTEM
        )
    }
}
