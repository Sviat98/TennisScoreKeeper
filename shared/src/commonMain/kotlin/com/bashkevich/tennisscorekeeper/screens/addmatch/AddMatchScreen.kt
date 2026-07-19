package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.AddMatchAppBar
import com.bashkevich.tennisscorekeeper.components.add_match.AddMatchComponent
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.navigation.SettingsFlowRoute

@Composable
fun AddMatchScreen(
    modifier: Modifier = Modifier,
    viewModel: AddMatchViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavHostController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedUiEffectHandler(
        effect = state.action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = { viewModel.consumeAction() }
    ) { action ->
        when (action) {
            is AddMatchAction.MatchAdded -> {
                navController.navigateUp()
            }
            is AddMatchAction.ShowUnauthorizedActionError -> {
                snackbarHostState.showUnauthorizedActionSnackbar(
                    navController = navController
                )
            }
            is AddMatchAction.ShowError -> {
                snackbarHostState.showSnackbar(message = action.message)
            }
        }
    }

    AddMatchContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = { viewModel.onEvent(it) },
        snackbarHostState = snackbarHostState,
        onBack = { navController.navigateUp() },
        onNavigateToSettings = { navController.navigate(SettingsFlowRoute) },
    )
}

@Composable
fun AddMatchContent(
    modifier: Modifier = Modifier,
    state: AddMatchState,
    onEvent: (AddMatchUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = {
            AddMatchAppBar(
                onBack = onBack,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val loadingState = state.loadingState) {
            is AddMatchLoadingState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AddMatchLoadingState.Content -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    AddMatchComponent(
                        contentState = loadingState,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}
