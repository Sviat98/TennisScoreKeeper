package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.MatchView
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListContent

@Composable
fun MatchDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchDetailsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    MatchDetailsContent(
        modifier = Modifier.then(modifier).fillMaxSize(),
        state = state
    )
}

@Composable
fun MatchDetailsContent(
    modifier: Modifier = Modifier,
    state: MatchDetailsState,
){

    val match = state.match
    Column(
        modifier = Modifier.then(modifier)
    ) {
        MatchView(
            match = match
        )
    }
}