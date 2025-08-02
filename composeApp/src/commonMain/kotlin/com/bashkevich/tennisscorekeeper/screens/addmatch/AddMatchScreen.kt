package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.AddMatchAppBar
import com.bashkevich.tennisscorekeeper.components.match.AddMatchComponent

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

    val navController = LocalNavHostController.current

    AddMatchContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = { viewModel.onEvent(it) },
        onNavigateAfterMatchAdd = { navController.navigateUp() }
    )
}

@Composable
fun AddMatchContent(
    modifier: Modifier = Modifier,
    state: AddMatchState,
    onEvent: (AddMatchUiEvent) -> Unit,
    onNavigateAfterMatchAdd: () -> Unit
) {
    val navController = LocalNavHostController.current
    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { AddMatchAppBar(onBack = { navController.navigateUp() }) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                AddMatchComponent(
                    state = state,
                    onEvent = onEvent,
                    onNavigateAfterMatchAdd = onNavigateAfterMatchAdd
                )
            }
        }
    }

}