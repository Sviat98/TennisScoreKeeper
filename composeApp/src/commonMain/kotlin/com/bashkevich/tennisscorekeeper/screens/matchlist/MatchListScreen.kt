package com.bashkevich.tennisscorekeeper.screens.matchlist

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
import com.bashkevich.tennisscorekeeper.components.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.match.MatchCard
import com.bashkevich.tennisscorekeeper.model.match.SimpleMatch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchListScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchListViewModel = koinViewModel(),
    onMatchClick: (SimpleMatch) -> Unit,
    onMatchAdd: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    MatchListContent(
        modifier = Modifier.then(modifier),
        state = state,
        onItemClick = onMatchClick,
        onCounterAdd = onMatchAdd
    )

}

@Composable
fun MatchListContent(
    modifier: Modifier = Modifier,
    state: MatchListState,
    onItemClick: (SimpleMatch) -> Unit,
    onCounterAdd: () -> Unit
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        floatingActionButton = {
            FloatingActionButton(onClick = onCounterAdd) {
                Icon(Icons.Filled.Add, contentDescription = "Add Match")
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.matches,
                    key = { it.id }) { match ->
                    MatchCard(
                        modifier = Modifier.hoverScaleEffect(),
                        match = match,
                        onClick = { onItemClick(match) }
                    )
                }

            }
        }
    }

}