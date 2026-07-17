package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.MatchListLoadingState
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.couldnt_load_data
import tennisscorekeeper.composeapp.generated.resources.match_list_empty
import tennisscorekeeper.composeapp.generated.resources.pull_down_to_update
import tennisscorekeeper.composeapp.generated.resources.theme_load_error

@Composable
fun MatchListScreen(
    modifier: Modifier = Modifier,
    matchListLoadingState: MatchListLoadingState,
    onItemClick: (ShortMatch) -> Unit
) {
    Box(modifier = modifier) {
        when (matchListLoadingState) {
            is MatchListLoadingState.Loading -> {
                // тут НЕ делаем verticalScroll, в состояни Loading refresh не должен быть доступен
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MatchListLoadingState.InitialError -> {
                Box(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        Text(stringResource(Res.string.couldnt_load_data), color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            stringResource(Res.string.pull_down_to_update),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is MatchListLoadingState.Content -> {
                if (matchListLoadingState.matches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(Res.string.match_list_empty),
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
                            Column(
                                modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ShortMatchScoreboardCard(
                                    modifier = Modifier.fillMaxWidth().hoverScaleEffect(),
                                    match = match,
                                    theme = matchListLoadingState.themes[match.themeId]
                                        ?: ScoreboardTheme.DEFAULT,
                                    onClick = { onItemClick(match) }
                                )
                                if (match.themeId != 0 && match.themeId !in matchListLoadingState.themes) {
                                    Text(
                                        stringResource(Res.string.theme_load_error),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
