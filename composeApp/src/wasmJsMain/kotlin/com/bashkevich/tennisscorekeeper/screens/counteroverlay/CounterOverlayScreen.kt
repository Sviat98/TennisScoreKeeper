package com.bashkevich.tennisscorekeeper.screens.counteroverlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.bashkevich.tennisscorekeeper.screens.counteroverlay.CounterOverlayViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.CounterView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CounterOverlayScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterOverlayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    CounterOverlayContent(
        modifier = Modifier.then(modifier),
        state = state
    )
}

@Composable
fun CounterOverlayContent(
    modifier: Modifier = Modifier,
    state: CounterOverlayState,
) {
    Box(modifier = Modifier.then(modifier)
        .drawBehind {
            drawRect(
                color = Color.Transparent,
                size = this.size,
                blendMode = BlendMode.Clear
            )
        }
        .fillMaxSize()) {
        CounterView(
            modifier = Modifier.align(Alignment.Center),
            counter = state.counter,
        )
    }
}