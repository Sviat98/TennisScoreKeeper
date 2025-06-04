package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.runtime.Immutable

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab

@Immutable
sealed class TournamentUiEvent : UiEvent {
    class SelectTab(val tournamentTab: TournamentTab): TournamentUiEvent()
}

@Immutable
data class TournamentState(
    val currentTab: TournamentTab
) : UiState {
    companion object {
        fun initial() = TournamentState(
            currentTab = TournamentTab.Matches
        )
    }
}

@Immutable
sealed class TournamentAction : UiAction {

}