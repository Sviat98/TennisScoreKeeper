package com.bashkevich.tennisscorekeeper.screens.counterdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.Button
import androidx.compose.material.Text
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.CounterView
import com.bashkevich.tennisscorekeeper.core.BASE_URL_FRONTEND
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CounterDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    CounterDetailsContent(
        modifier = Modifier.then(modifier),
        state = state
    )
}

@Composable
fun CounterDetailsContent(
    modifier: Modifier = Modifier,
    state: CounterDetailsState
) {
    val clipboardManager = LocalClipboardManager.current
    Column(
        modifier = Modifier.then(modifier).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val counter = state.counter
        CounterView(counter = counter)

        Button(onClick = {
            clipboardManager.setText(AnnotatedString("$BASE_URL_FRONTEND#com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute/${counter.id}"))
        }){
            Text("Copy Control Panel URL")
        }
        Button(onClick = {
            clipboardManager.setText(AnnotatedString("$BASE_URL_FRONTEND#com.bashkevich.tennisscorekeeper.navigation.CounterOverlayRoute?counterId=${counter.id}"))
        }){
            Text("Copy Overlay URL")
        }
    }
}