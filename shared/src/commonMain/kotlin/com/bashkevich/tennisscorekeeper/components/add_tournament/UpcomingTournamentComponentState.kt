package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.tournament.domain.UpcomingTournament

@Immutable
data class UpcomingTournamentComponentState(
    val selectedTournament: UpcomingTournament?,
    val upcomingTournamentsOptionsState: UpcomingTournamentsOptionsState,
) {

    @Immutable
    sealed interface UpcomingTournamentsOptionsState {

        @Immutable
        data object Loading : UpcomingTournamentsOptionsState

        @Immutable
        data object Error : UpcomingTournamentsOptionsState

        @Immutable
        data class Idle(val tournaments: List<UpcomingTournament>) : UpcomingTournamentsOptionsState
    }
}
