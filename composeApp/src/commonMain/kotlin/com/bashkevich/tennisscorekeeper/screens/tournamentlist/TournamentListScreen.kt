package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.TournamentListAppBar
import com.bashkevich.tennisscorekeeper.components.TournamentListAppBarWithButton
import com.bashkevich.tennisscorekeeper.components.modifier.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.modifier.refreshByKeyboard
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Add
import com.bashkevich.tennisscorekeeper.components.tournament.TournamentListItem
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.SettingsFlowRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.add_tournament
import tennisscorekeeper.composeapp.generated.resources.try_again

@Composable
fun TournamentListScreen(
    modifier: Modifier = Modifier,
    viewModel: TournamentListViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val loadingState = state.loadingState) {
        is TournamentListLoadingState.Loading -> {
            TournamentListLoading(
                modifier = modifier,
            )
        }

        is TournamentListLoadingState.Content -> {
            TournamentListContent(
                modifier = modifier,
                loadingState = loadingState,
                action = state.action,
                onRefresh = { viewModel.onEvent(TournamentListUiEvent.RefreshTournaments) },
                onConsumeAction = { viewModel.consumeAction() },
            )
        }

        is TournamentListLoadingState.Error -> {
            TournamentListError(
                modifier = modifier,
                loadingState = loadingState,
                onRetry = { viewModel.onEvent(TournamentListUiEvent.Retry) },
            )
        }
    }
}

@Composable
private fun TournamentListContent(
    modifier: Modifier = Modifier,
    loadingState: TournamentListLoadingState.Content,
    action: TournamentListAction?,
    onRefresh: () -> Unit,
    onConsumeAction: () -> Unit,
) {
    val navController = LocalNavHostController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedUiEffectHandler(
        effect = action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = onConsumeAction
    ) { currentAction ->
        when (currentAction) {
            is TournamentListAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    Scaffold(
        modifier = Modifier.then(modifier).refreshByKeyboard(onRefresh),
        topBar = {
            TournamentListAppBarWithButton(
                onNavigateToSettings = { navController.navigate(SettingsFlowRoute) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AddTournamentRoute) }) {
                Icon(IconGroup.Default.Add, contentDescription = stringResource(Res.string.add_tournament))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = loadingState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    loadingState.tournaments,
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
private fun TournamentListLoading(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
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
private fun TournamentListError(
    modifier: Modifier = Modifier,
    loadingState: TournamentListLoadingState.Error,
    onRetry: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TournamentListAppBar() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(loadingState.message)
            Button(onClick = onRetry) {
                Text(stringResource(Res.string.try_again))
            }
        }
    }
}
