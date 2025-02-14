package com.bashkevich.tennisscorekeeper.screens.counterlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.CounterCard
import com.bashkevich.tennisscorekeeper.components.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CounterListScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterListViewModel = koinViewModel(),
    onCounterClick: (Counter) -> Unit,
    onCounterAdd: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    CounterListContent(
        modifier = Modifier.then(modifier).fillMaxSize(),
        state = state,
        onItemClick = onCounterClick,
        onCounterAdd = onCounterAdd
    )

}

@Composable
fun CounterListContent(
    modifier: Modifier = Modifier,
    state: CounterListState,
    onItemClick: (Counter) -> Unit,
    onCounterAdd: () -> Unit
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        floatingActionButton = {
            FloatingActionButton(onClick = onCounterAdd) {
                Icon(Icons.Filled.Add, contentDescription = "Add Counter")
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.counters,
                    key = { it.id }) { counter ->
                    CounterCard(
                        modifier = Modifier.hoverScaleEffect(),
                        counter = counter,
                        onClick = { onItemClick(counter) }
                    )
                }

            }
        }
    }

}