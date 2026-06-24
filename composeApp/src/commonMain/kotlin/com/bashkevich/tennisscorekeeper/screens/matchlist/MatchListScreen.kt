package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.modifier.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.scoreboard.short.ShortMatchScoreboardCard
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.MatchListLoadingState

@Composable
fun MatchListScreen(
    modifier: Modifier = Modifier,
    matchListLoadingState: MatchListLoadingState,
    onItemClick: (ShortMatch) -> Unit
) {
    Box(modifier = modifier) {
        when (matchListLoadingState) {
            is MatchListLoadingState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MatchListLoadingState.InitialError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.layout.Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        Text("Couldn't load data", color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Pull down to update",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is MatchListLoadingState.Content -> {
                if (matchListLoadingState.matches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Match list is empty",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(
                            matchListLoadingState.matches,
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
        }
    }
}
