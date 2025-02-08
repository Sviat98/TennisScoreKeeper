package com.bashkevich.tennisscorekeeper.screens.counterlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.bashkevich.tennisscorekeeper.model.counter.COUNTERS
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CounterListScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterListViewModel = koinViewModel(),
    onCounterClick: (Counter) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    CounterListContent(
        modifier = Modifier.then(modifier).fillMaxSize(),
        state = state,
        onItemClick = onCounterClick
    )

}

@Composable
fun CounterListContent(
    modifier: Modifier = Modifier,
    state: CounterListState,
    onItemClick: (Counter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.then(modifier),
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