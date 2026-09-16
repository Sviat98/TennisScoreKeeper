package com.bashkevich.tennisscorekeeper.screens.editmatch

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.add_match.participant.ParticipantDisplayNameComponent
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Check
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponent
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.edit_match
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.save

@Composable
fun EditMatchScreen(
    modifier: Modifier = Modifier,
    viewModel: EditMatchViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navController = LocalNavHostController.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedUiEffectHandler(
        effect = state.action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = { viewModel.consumeAction() }
    ) { currentAction ->
        when (currentAction) {
            is EditMatchAction.MatchSaved -> navController.navigateUp()

            is EditMatchAction.ShowUnauthorizedError ->
                snackbarHostState.showUnauthorizedActionSnackbar(navController = navController)

            is EditMatchAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = {
            EditMatchTopAppBar(
                isSaveEnabled = state.match != null && !state.isSaving,
                onBack = { navController.navigateUp() },
                onSave = { viewModel.onEvent(EditMatchUiEvent.SaveMatch) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val editedMatch = state.editedMatch

        if (editedMatch == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EditMatchContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                state = state,
                editedMatch = editedMatch,
                onEvent = { viewModel.onEvent(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMatchTopAppBar(
    isSaveEnabled: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.edit_match)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onSave, enabled = isSaveEnabled) {
                Icon(
                    imageVector = IconGroup.Default.Check,
                    contentDescription = stringResource(Res.string.save)
                )
            }
        }
    )
}

@Composable
private fun EditMatchContent(
    modifier: Modifier = Modifier,
    state: EditMatchState,
    editedMatch: Match,
    onEvent: (EditMatchUiEvent) -> Unit,
) {
    Box(
        modifier = Modifier.then(modifier)
            .fillMaxWidth()
            .padding(all = 16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchDetailsScoreboardView(
                modifier = Modifier.horizontalScroll(state = rememberScrollState()),
                match = editedMatch,
                theme = state.theme,
            )

            ParticipantDisplayNameComponent(
                participant = editedMatch.firstParticipant,
                onParticipantDisplayNameChange = { displayName ->
                    onEvent(EditMatchUiEvent.ChangeDisplayName(1, displayName))
                }
            )

            ParticipantDisplayNameComponent(
                participant = editedMatch.secondParticipant,
                onParticipantDisplayNameChange = { displayName ->
                    onEvent(EditMatchUiEvent.ChangeDisplayName(2, displayName))
                }
            )

            ThemeComponent(
                themeComponentState = state.themeComponentState,
                onThemesFetch = { onEvent(EditMatchUiEvent.FetchThemes) },
                onThemeSelected = { theme ->
                    onEvent(EditMatchUiEvent.SelectTheme(theme.id))
                },
                onRetrySelectedTheme = { themeId ->
                    onEvent(EditMatchUiEvent.RetrySelectedTheme(themeId))
                },
                showPreviewButton = false
            )
        }
    }
}
