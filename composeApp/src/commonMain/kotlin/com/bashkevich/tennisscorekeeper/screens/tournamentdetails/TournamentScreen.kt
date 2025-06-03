package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
fun TournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: TournamentViewModel,
    onTabSelected: (TournamentTab) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    val currentTab = state.currentTab

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = TournamentTab.Matches.toRouteString(),
                modifier = Modifier.clickable { onTabSelected(TournamentTab.Matches) },
                color = if (currentTab == TournamentTab.Matches) Color.Red else Color.DarkGray
            )
            Text(
                text = TournamentTab.Participants.toRouteString(),
                modifier = Modifier.clickable { onTabSelected(TournamentTab.Participants) },
                color = if (currentTab == TournamentTab.Participants) Color.Red else Color.DarkGray
            )
        }
        Text("This is ${currentTab.toRouteString()} tab")
    }

}