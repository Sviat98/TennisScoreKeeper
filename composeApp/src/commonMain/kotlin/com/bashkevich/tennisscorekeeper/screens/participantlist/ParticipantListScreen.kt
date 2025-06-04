package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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

    Column {
        Text("This is a participant screen")
    }
}