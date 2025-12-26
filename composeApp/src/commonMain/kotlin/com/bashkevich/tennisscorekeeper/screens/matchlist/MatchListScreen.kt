package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.scoreboard.short.ShortMatchScoreboardCard
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentState
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentUiEvent

@Composable
fun MatchListScreen(
    modifier: Modifier = Modifier,
    state: TournamentState,
    onEvent: (TournamentUiEvent) -> Unit
) {
    val navController = LocalNavHostController.current

    MatchListContent(
        modifier = Modifier.then(modifier),
        state = state,
        onItemClick = { match -> navController.navigate(MatchDetailsRoute(match.id)) },
    )

}

@Composable
fun MatchListContent(
    modifier: Modifier = Modifier,
    state: TournamentState,
    onItemClick: (ShortMatch) -> Unit,
) {
    val matchListState = state.matchListState
    Box(
        modifier = Modifier.then(modifier)
    ) {
        LazyColumn(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                matchListState.matches,
                key = { it.id }) { match ->
                ShortMatchScoreboardCard(
                    modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth().hoverScaleEffect(),
                    match = match,
                    onClick = { onItemClick(match) }
                )
            }
        }
    }
}