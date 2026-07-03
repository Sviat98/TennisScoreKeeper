package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.MatchDetailsAppBar
import com.bashkevich.tennisscorekeeper.components.expect.MatchDetailsContentWrapper
import com.bashkevich.tennisscorekeeper.components.expect.setText
import com.bashkevich.tennisscorekeeper.components.match_details.MatchStatusButton
import com.bashkevich.tennisscorekeeper.components.match_details.ParticipantsPointsControlPanel
import com.bashkevich.tennisscorekeeper.components.match_details.RetireParticipantPanel
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ChooseServePanel
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.convertToString
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.navigation.SettingsRoute
import kotlinx.coroutines.launch

@Composable
fun MatchDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchDetailsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val navController = LocalNavHostController.current

    LaunchedUiEffectHandler(
        effect = state.action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = { viewModel.consumeAction() }
    ) { currentAction ->
        println("action = $currentAction")
        when (currentAction) {
            is MatchDetailsAction.ShowUnauthorizedError ->
                snackbarHostState.showUnauthorizedActionSnackbar(
                    navController = navController)
    }
}

Box(modifier = Modifier.then(modifier).fillMaxSize()) {
    MatchDetailsContentWrapper(
        modifier = Modifier.fillMaxSize(),
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = { viewModel.onEvent(it) }
    )

    if (state.connectionState == ConnectionState.Disconnected) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = true, onClick = { })
                .alpha(0.7f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Connection with scoreboard lost",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
}

// вызывается из MatchDetailsContentWrapper (Desktop/WasmJS)
@Composable
fun MatchDetailsCommonContent(
    modifier: Modifier = Modifier,
    state: MatchDetailsState,
    snackbarHostState: SnackbarHostState,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {
    val navController = LocalNavHostController.current
    val match = state.match

    val clipboard = LocalClipboard.current

    val isAuthorized = LocalAuthorization.current

    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.then(modifier),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MatchDetailsAppBar(
                matchId = match.id,
                onBack = { navController.navigateUp() },
                onCopyLink = { link ->
                    scope.launch {
                        clipboard.setText(link)
                    }
                },
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxWidth().padding(paddingValues).padding(all = 16.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().align(Alignment.Center),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MatchDetailsScoreboardView(
                    modifier = Modifier.horizontalScroll(state = rememberScrollState()),
                    match = match,
                )

                Text("Status: ${match.status.convertToString()}")

                MatchStatusButton(
                    match = match,
                    onStatusChange = { status ->
                        onEvent(
                            MatchDetailsUiEvent.ChangeMatchStatus(
                                status = status
                            )
                        )
                    }
                )

                when (match.status) {
                    MatchStatus.NOT_STARTED -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        ChooseServePanel(
                            modifier = Modifier.fillMaxWidth(),
                            match = match,
                            onFirstParticipantToServeChoose = { participantId ->
                                onEvent(
                                    MatchDetailsUiEvent.SetFirstParticipantToServe(
                                        participantId = participantId
                                    )
                                )
                            },
                            onFirstPlayerInPairToServeChoose = { playerId ->
                                onEvent(
                                    MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                        playerId = playerId
                                    )
                                )
                            }
                        )
                    }

                    MatchStatus.IN_PROGRESS -> {
                        ParticipantsPointsControlPanel(
                            modifier = Modifier.fillMaxWidth(),
                            match = match,
                            onUpdateScore = { participantId, scoreType ->
                                onEvent(
                                    MatchDetailsUiEvent.UpdateScore(
                                        participantId = participantId,
                                        scoreType = scoreType
                                    )
                                )
                            },
                            onUndoPoint = { onEvent(MatchDetailsUiEvent.UndoPoint) },
                            onRedoPoint = { onEvent(MatchDetailsUiEvent.RedoPoint) }
                        )
                    }

                    MatchStatus.PAUSED -> {
                        RetireParticipantPanel(
                            modifier = Modifier.fillMaxWidth(),
                            match = match,
                            onParticipantRetire = { participantId ->
                                onEvent(
                                    MatchDetailsUiEvent.SetParticipantRetired(
                                        participantId = participantId
                                    )
                                )
                            }
                        )
                    }

                    else -> {}
                }
            }
        }


    }

}
