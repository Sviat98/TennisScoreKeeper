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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

    when (state.loadingState) {
        TournamentListContentState.Loading -> {
            TournamentListLoading(
                modifier = Modifier.then(modifier),
            )
        }

        TournamentListContentState.Refreshing,
        TournamentListContentState.Idle -> {
            TournamentListContent(
                modifier = Modifier.then(modifier),
                state = state,
                viewModel = viewModel,
            )
        }

        is TournamentListContentState.InitialError -> {
            TournamentListError(
                modifier = Modifier.then(modifier),
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }
    }

}

@Composable
fun TournamentListContent(
    modifier: Modifier = Modifier,
    state: TournamentListState,
    viewModel: TournamentListViewModel,
) {
    val navController = LocalNavHostController.current
    val isAuthorized = LocalAuthorization.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
            when (action) {
                is TournamentListAction.ShowRefreshError -> {
                    snackbarHostState.showSnackbar(message = action.message)
                }
            }
        }
    }

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.loadingState is TournamentListContentState.Refreshing,
            onRefresh = { viewModel.onEvent(TournamentListUiEvent.RefreshTournaments) },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
