package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.TournamentListAppBar
import com.bashkevich.tennisscorekeeper.components.TournamentListAppBarWithButton
import com.bashkevich.tennisscorekeeper.components.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Add
import com.bashkevich.tennisscorekeeper.components.tournament.TournamentListItem
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.ProfileRoute
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

    when (state.loadingState) {
        TournamentListLoadingState.Loading -> {
            TournamentListLoading(
                modifier = Modifier.then(modifier),
            )
        }

        TournamentListLoadingState.Error -> {
            TournamentListError(
                modifier = Modifier.then(modifier),
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }

        TournamentListLoadingState.Success -> {
            TournamentListContent(
                modifier = Modifier.then(modifier),
                state = state
            )
        }
    }

}

@Composable
fun TournamentListContent(
    modifier: Modifier = Modifier,
    state: TournamentListState,
) {
    val navController = LocalNavHostController.current

    val isAuthorized = LocalAuthorization.current
    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = {
            TournamentListAppBarWithButton(
                isAuthorized = isAuthorized,
                onNavigateToLoginOrProfile = {
                    if (isAuthorized) {
                        navController.navigate(ProfileRoute)
                    } else {
                        navController.navigate(LoginRoute)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AddTournamentRoute) }) {
                Icon(IconGroup.Default.Add, contentDescription = "Add Tournament")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                        onTournamentClick = { navController.navigate(TournamentRoute(tournament.id)) }
                    )
                }

            }
        }
    }
}

@Composable
fun TournamentListLoading(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { TournamentListAppBar() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun TournamentListError(
    modifier: Modifier = Modifier,
    onEvent: (TournamentListUiEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { TournamentListAppBar() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text("Error happened!")
            Button(onClick = { onEvent(TournamentListUiEvent.LoadTournaments) }) {
                Text("Try Again")
            }
        }
    }
}

