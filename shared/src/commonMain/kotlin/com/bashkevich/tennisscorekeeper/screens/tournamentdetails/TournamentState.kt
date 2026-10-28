package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab

@Immutable
sealed interface TournamentDetailsLoadingState {
    data object Loading : TournamentDetailsLoadingState
    data class Content(val tournament: Tournament) : TournamentDetailsLoadingState
}

@Immutable
sealed interface MatchListLoadingState {
    data object Loading : MatchListLoadingState
    data object InitialError : MatchListLoadingState
    data class Content(
        val matches: List<ShortMatch>,
        val themes: Map<Int, ScoreboardTheme> = emptyMap()
    ) : MatchListLoadingState
}

@Immutable
sealed interface ParticipantListLoadingState {
    data object Loading : ParticipantListLoadingState
    data object InitialError : ParticipantListLoadingState
    data class Content(
        val participants: List<TennisParticipant>,
        val isUploadInProgress: Boolean = false,
        val seedAmount: Int
    ) : ParticipantListLoadingState
}

@Immutable
sealed class TournamentUiEvent : UiEvent {
    class ChangeTournamentStatus(val tournamentStatus: TournamentStatus) : TournamentUiEvent()
    class ChangeSeedAmount(val seedAmount: Int) : TournamentUiEvent()
    class UploadParticipants(val seedAmount: Int) : TournamentUiEvent()
    class SwitchTab(val tab: TournamentTab) : TournamentUiEvent()
    data object Refresh : TournamentUiEvent()
}

@Immutable
data class TournamentState(
    val tournamentDetailsState: TournamentDetailsLoadingState = TournamentDetailsLoadingState.Loading,
    val matchListLoadingState: MatchListLoadingState = MatchListLoadingState.Loading,
    val participantListLoadingState: ParticipantListLoadingState = ParticipantListLoadingState.Loading,
    val activeTab: TournamentTab = TournamentTab.MATCHES,
    val isRefreshing: Boolean = false,
    val action: TournamentAction? = null
) : UiState {
    companion object {
        fun initial() = TournamentState()
    }
}

@Immutable
sealed class TournamentAction : UiAction {
    data class ShowError(val message: String) : TournamentAction()
    data object ShowUnauthorizedError : TournamentAction()
}