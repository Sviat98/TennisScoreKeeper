package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

@Composable
fun AddMatchScreen(
    modifier: Modifier = Modifier,
    viewModel: AddMatchViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    AddMatchContent(
        modifier = Modifier.then(modifier),
        state = state,
        onMatchAdded = {}
    )
}

@Composable
fun AddMatchContent(
    modifier: Modifier = Modifier,
    state: AddMatchState,
    onMatchAdded: () -> Unit
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }else{
            if (state.tournament.type == TournamentType.SINGLES){
                Text("Add Singles Match Screen")
            }else{
                Text("Add Doubles Match Screen")
            }
        }
    }
}