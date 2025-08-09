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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.DefaultLoadingComponent
import com.bashkevich.tennisscorekeeper.components.TournamentDetailsAppBar
import com.bashkevich.tennisscorekeeper.components.tournament.ChangeTournamentStatusButton
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.convertToString
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab
import com.bashkevich.tennisscorekeeper.navigation.toDisplayString
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListScreen
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListScreen
import kotlinx.coroutines.launch

@Composable
fun TournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: TournamentViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    if (state.isLoading) {
        DefaultLoadingComponent(
            modifier = Modifier.then(modifier),
            progressIndicatorSize = 128.dp
        )
    } else {
        TournamentContent(
            modifier = Modifier.then(modifier),
            state = state,
            onEvent = { viewModel.onEvent(it) },
        )
    }

}

@Composable
fun TournamentContent(
    modifier: Modifier = Modifier,
    state: TournamentState,
    onEvent: (TournamentUiEvent) -> Unit,
) {
    val tournament = state.tournament

    val navController = LocalNavHostController.current
    val matchListState = state.matchListState

    val uncompletedMatches =
        matchListState.matches.filter { it.status != MatchStatus.COMPLETED }.size

    val participantListState = state.participantListState

    val participantsAmount = participantListState.participants.size

    val pagerState = rememberPagerState(pageCount = { 2 })

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { TournamentDetailsAppBar(onBack = { navController.navigateUp() }) }
    ) {
        Column(
            modifier = Modifier.padding(it).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(tournament.name, fontSize = 28.sp)
                    Text("Status: ${tournament.status.convertToString()}")
                }
                ChangeTournamentStatusButton(
                    modifier = Modifier.weight(1f),
                    status = tournament.status,
                    onStatusChange = { status ->
                        onEvent(
                            TournamentUiEvent.ChangeTournamentStatus(
                                status
                            )
                        )
                    },
                    uncompletedMatches = uncompletedMatches,
                    participantsAmount = participantsAmount
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()
                        .background(color = Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
                        .padding(8.dp).align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = TournamentTab.MATCHES.toDisplayString(),
                        modifier = Modifier.clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(TournamentTab.MATCHES.ordinal)
                            }
                        },
                        color = if (pagerState.currentPage == TournamentTab.MATCHES.ordinal) MaterialTheme.colors.primary else Color.DarkGray
                    )
                    Text(
                        text = TournamentTab.PARTICIPANTS.toDisplayString(),
                        modifier = Modifier.clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(TournamentTab.PARTICIPANTS.ordinal)
                            }
                        },
                        color = if (pagerState.currentPage == TournamentTab.PARTICIPANTS.ordinal) MaterialTheme.colors.primary else Color.DarkGray
                    )
                }

            }

            HorizontalPager(state = pagerState) { index ->
                when (index) {
                    TournamentTab.MATCHES.ordinal -> MatchListScreen(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        onEvent = onEvent
                    )
                    TournamentTab.PARTICIPANTS.ordinal -> ParticipantListScreen(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }

}