package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab
import com.bashkevich.tennisscorekeeper.navigation.toRouteString
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListScreen
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListViewModel
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListScreen
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListViewModel
import org.koin.compose.viewmodel.koinViewModel

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


    TournamentContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = { viewModel.onEvent(it) },
    )
}

@Composable
fun TournamentContent(
    modifier: Modifier = Modifier,
    state: TournamentState,
    onEvent: (TournamentUiEvent) -> Unit,
) {
    val currentTab = state.currentTab

    val matchListViewModel = koinViewModel<MatchListViewModel>()

    val participantListViewModel = koinViewModel<ParticipantListViewModel>()

    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = TournamentTab.Matches.toRouteString(),
                modifier = Modifier.clickable {
                    onEvent(TournamentUiEvent.SelectTab(TournamentTab.Matches))
                },
                color = if (currentTab == TournamentTab.Matches) Color.Red else Color.DarkGray
            )
            Text(
                text = TournamentTab.Participants.toRouteString(),
                modifier = Modifier.clickable {
                    onEvent(TournamentUiEvent.SelectTab(TournamentTab.Participants))
                },
                color = if (currentTab == TournamentTab.Participants) Color.Red else Color.DarkGray
            )
        }

        HorizontalPager(state = pagerState) { _ ->
            when (currentTab) {
                TournamentTab.Matches -> MatchListScreen(
                    viewModel = matchListViewModel,
                )
                TournamentTab.Participants -> ParticipantListScreen(viewModel = participantListViewModel)
            }
        }
    }
}