package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.MatchDetailsAppBar
import com.bashkevich.tennisscorekeeper.components.expect.MatchDetailsContentWrapper
import com.bashkevich.tennisscorekeeper.components.expect.setText
import com.bashkevich.tennisscorekeeper.components.match_details.ScoreboardControlPanel
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeLoadErrorRow
import com.bashkevich.tennisscorekeeper.model.match.remote.body.toResource
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardThemeState
import com.bashkevich.tennisscorekeeper.model.theme.domain.themeOrDefault
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.navigation.SettingsFlowRoute
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.status

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
                    navController = navController
                )

            is MatchDetailsAction.ShowError ->
                snackbarHostState.showSnackbar(
                    message = currentAction.message
                )
        }
    }

    if (state.connectionState == ConnectionState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        MatchDetailsContentWrapper(
            modifier = Modifier.fillMaxSize(),
            state = state,
            snackbarHostState = snackbarHostState,
            onEvent = { viewModel.onEvent(it) }
        )
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
                onNavigateToSettings = { navController.navigate(SettingsFlowRoute) }
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
                    theme = state.themeState.themeOrDefault(),
                )

                if (state.themeState is ScoreboardThemeState.Error) {
                    ThemeLoadErrorRow(onRetry = { onEvent(MatchDetailsUiEvent.RetryThemeLoad) })
                }

                val statusText = stringResource(match.status.toResource())
                Text("${stringResource(Res.string.status)}: $statusText")

                ScoreboardControlPanel(
                    connectionState = state.connectionState,
                    match = match,
                    onEvent = onEvent,
                )
            }
        }


    }

}
