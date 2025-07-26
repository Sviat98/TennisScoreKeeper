package com.bashkevich.tennisscorekeeper.screens.counterdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
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
        state = state,
        onEvent = { viewModel.onEvent(it) }
    )
}

@Composable
fun CounterDetailsContent(
    modifier: Modifier = Modifier,
    state: CounterDetailsState,
    onEvent: (CounterDetailsUiEvent) -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val clipboard = LocalClipboard.current
    Column(
        modifier = Modifier.then(modifier).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val counter = state.counter
        CounterView(counter = counter)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                onEvent(
                    CounterDetailsUiEvent.ChangeCounterValue(
                        counter.id,
                        -1
                    )
                )
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Counter back")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Counter back")
            }
            Button(onClick = {
                onEvent(
                    CounterDetailsUiEvent.ChangeCounterValue(
                        counter.id,
                        1
                    )
                )
            }) {
                Text("Counter forward")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Counter forward")
            }
        }
        val scope = rememberCoroutineScope()

        Button(onClick = {
//            scope.launch {
//                clipboard.setClipEntry(ClipEntry(clipMetadata = AnnotatedString("$BASE_URL_FRONTEND#com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute/${counter.id}"))
//            }
            clipboardManager.setText(AnnotatedString("$BASE_URL_FRONTEND#com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute/${counter.id}"))
        }) {
            Text("Copy Control Panel URL")
        }
        Button(onClick = {
            clipboardManager.setText(AnnotatedString("$BASE_URL_FRONTEND#com.bashkevich.tennisscorekeeper.navigation.CounterOverlayRoute?counterId=${counter.id}"))
        }) {
            Text("Copy Overlay URL")
        }
    }
}