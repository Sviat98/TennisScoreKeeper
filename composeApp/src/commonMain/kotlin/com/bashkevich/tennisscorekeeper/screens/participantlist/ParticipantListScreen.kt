package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.bashkevich.tennisscorekeeper.components.participant.ParticipantCard

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    viewModel: ParticipantListViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    ParticipantListContent(
        modifier = Modifier.then(modifier),
        state = state,
        onParticipantAdd = {}
    )
}

@Composable
fun ParticipantListContent(
    modifier: Modifier = Modifier,
    state: ParticipantListState,
    onParticipantAdd: () -> Unit
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        floatingActionButton = {
            FloatingActionButton(onClick = onParticipantAdd) {
                Icon(Icons.Filled.Add, contentDescription = "Add Participant")
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    state.participants,
                    key = { it.id }) { participant ->
                    ParticipantCard(participant = participant)
                }
            }
        }
    }

}