package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.tournament.domain.TOURNAMENT_DEFAULT
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus

import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListState
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListState


@Immutable
sealed class TournamentUiEvent : UiEvent {
    class ChangeTournamentStatus(val tournamentStatus: TournamentStatus) : TournamentUiEvent()
    class SelectTab(val tournamentTab: TournamentTab) : TournamentUiEvent()

    class SelectFile(val file: ExcelFile) : TournamentUiEvent()
    data object UploadFile : TournamentUiEvent()
}

@Immutable
data class TournamentState(
    val isLoading: Boolean,
    val tournament: Tournament,
    val currentTab: TournamentTab,
    val matchListState: MatchListState,
    val participantListState: ParticipantListState,
) : UiState {
    companion object {
        fun initial() = TournamentState(
            isLoading = true,
            tournament = TOURNAMENT_DEFAULT,
            currentTab = TournamentTab.Matches,
            matchListState = MatchListState.initial(),
            participantListState = ParticipantListState.initial()
        )
    }
}

@Immutable
sealed class TournamentAction : UiAction {

}