package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.TournamentListAppBar
import com.bashkevich.tennisscorekeeper.components.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.tournament.TournamentListItem
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute

@Composable
fun TournamentListScreen(
    modifier: Modifier = Modifier,
    viewModel: TournamentListViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    val navController = LocalNavHostController.current

    TournamentListContent(
        modifier = Modifier.then(modifier),
        state = state,
        onTournamentAdd = { navController.navigate(AddTournamentRoute) },
        onTournamentClick = { tournament -> navController.navigate(TournamentRoute(tournament.id)) }
    )
}

@Composable
fun TournamentListContent(
    modifier: Modifier = Modifier,
    state: TournamentListState,
    onTournamentAdd: () -> Unit,
    onTournamentClick: (Tournament) -> Unit
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { TournamentListAppBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = onTournamentAdd) {
                Icon(Icons.Filled.Add, contentDescription = "Add Tournament")
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    state.tournaments,
                    key = { it.id }) { tournament ->
                    TournamentListItem(
                        modifier = Modifier
                            .widthIn(max = 360.dp)
                            .fillMaxWidth()
                            .hoverScaleEffect(),
                        tournament = tournament,
                        onTournamentClick = onTournamentClick
                    )
                }

            }
        }
    }
}

