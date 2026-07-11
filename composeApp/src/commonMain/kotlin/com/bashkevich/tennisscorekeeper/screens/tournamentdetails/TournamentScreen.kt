package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.TournamentDetailsAppBar
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.modifier.refreshByKeyboard
import com.bashkevich.tennisscorekeeper.components.tournament.ChangeTournamentStatusButton
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Add
import com.bashkevich.tennisscorekeeper.components.tournament.TournamentDetailsLoading
import com.bashkevich.tennisscorekeeper.model.tournament.remote.toResource
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.SettingsRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListScreen
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.add_match
import tennisscorekeeper.composeapp.generated.resources.matches
import tennisscorekeeper.composeapp.generated.resources.participants
import tennisscorekeeper.composeapp.generated.resources.status

@Composable
fun TournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: TournamentViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TournamentContent(
        modifier = modifier,
        state = state,
        action = state.action,
        tournamentDetailsState = state.tournamentDetailsState,
        onEvent = { viewModel.onEvent(it) },
        onRefresh = { viewModel.onEvent(TournamentUiEvent.Refresh) },
        onConsumeAction = { viewModel.consumeAction() },
    )
}

@Composable
private fun TournamentContent(
    modifier: Modifier = Modifier,
    state: TournamentState,
    action: TournamentAction?,
    tournamentDetailsState: TournamentDetailsLoadingState,
    onEvent: (TournamentUiEvent) -> Unit,
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
            is TournamentAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
            is TournamentAction.ShowUnauthorizedError ->
                snackbarHostState.showUnauthorizedActionSnackbar(navController = navController)
        }
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.then(modifier).refreshByKeyboard(onRefresh),
        topBar = {
            TournamentDetailsAppBar(
                onBack = { navController.navigateUp() },
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        },
        floatingActionButton = {
            if (tournamentDetailsState is TournamentDetailsLoadingState.Content) {
                val tournament = tournamentDetailsState.tournament

                if (tournament.status == TournamentStatus.IN_PROGRESS) {
                    FloatingActionButton(
                        onClick = {
                            when (state.activeTab) {
                                TournamentTab.MATCHES -> {
                                    navController.navigate(AddMatchRoute(tournament.id))
                                }

                                TournamentTab.PARTICIPANTS -> {

                                }
                            }
                        }
                    ) {
                        Icon(
                            IconGroup.Default.Add,
                            contentDescription = stringResource(Res.string.add_match)
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (tournamentDetailsState) {
                    is TournamentDetailsLoadingState.Loading -> {
                        TournamentDetailsLoading(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is TournamentDetailsLoadingState.Content -> {
                        val tournament = tournamentDetailsState.tournament
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(tournament.name, fontSize = 28.sp)
                                val statusText = stringResource(tournament.status.toResource())
                                Text("${stringResource(Res.string.status)}: $statusText")
                            }
                            ChangeTournamentStatusButton(
                                modifier = Modifier.weight(1f),
                                status = tournament.status,
                                onStatusChange = { status ->
                                    onEvent(TournamentUiEvent.ChangeTournamentStatus(status))
                                },
                                uncompletedMatches = tournament.uncompletedMatches,
                                participantsAmount = tournament.totalParticipants
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp)
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = stringResource(Res.string.matches),
                            modifier = Modifier.clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(TournamentTab.MATCHES.ordinal)
                                    onEvent(TournamentUiEvent.SwitchTab(TournamentTab.MATCHES))
                                }
                            },
                            color = if (pagerState.currentPage == TournamentTab.MATCHES.ordinal)
                                MaterialTheme.colorScheme.primary else Color.DarkGray
                        )
                        Text(
                            text = stringResource(Res.string.participants),
                            modifier = Modifier.clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(TournamentTab.PARTICIPANTS.ordinal)
                                    onEvent(TournamentUiEvent.SwitchTab(TournamentTab.PARTICIPANTS))
                                }
                            },
                            color = if (pagerState.currentPage == TournamentTab.PARTICIPANTS.ordinal)
                                MaterialTheme.colorScheme.primary else Color.DarkGray
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    when (index) {
                        TournamentTab.MATCHES.ordinal -> MatchListScreen(
                            modifier = Modifier.fillMaxSize(),
                            matchListLoadingState = state.matchListLoadingState,
                            onItemClick = { match -> navController.navigate(MatchDetailsRoute(match.id)) }
                        )

                        TournamentTab.PARTICIPANTS.ordinal -> ParticipantListScreen(
                            modifier = Modifier.fillMaxSize(),
                            participantListLoadingState = state.participantListLoadingState,
                            tournamentStatus = (tournamentDetailsState as? TournamentDetailsLoadingState.Content)?.tournament?.status
                                ?: TournamentStatus.NOT_STARTED,
                            onUploadFile = { onEvent(TournamentUiEvent.UploadFile) },
                            onSelectFile = { file -> onEvent(TournamentUiEvent.SelectFile(file)) }
                        )
                    }
                }
            }
        }
    }
}