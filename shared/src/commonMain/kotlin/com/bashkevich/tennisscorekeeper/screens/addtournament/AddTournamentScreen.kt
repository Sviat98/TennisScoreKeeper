package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.AddTournamentAppBar
import com.bashkevich.tennisscorekeeper.components.add_match.MatchScoringAndThemeSettingsBlock
import com.bashkevich.tennisscorekeeper.components.add_tournament.TournamentNameAndTypeComponent
import com.bashkevich.tennisscorekeeper.components.dialog.ScoreboardThemePreviewDialog
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.navigation.SettingsFlowRoute
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.add

@Composable
fun AddTournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTournamentViewModel,
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
            is AddTournamentAction.TournamentAdded -> {
                navController.navigateUp()
            }

            is AddTournamentAction.ShowUnauthorizedActionError -> {
                snackbarHostState.showUnauthorizedActionSnackbar(
                    navController = navController
                )
            }

            is AddTournamentAction.ShowError -> {
                snackbarHostState.showSnackbar(message = action.message)
            }
        }
    }

    AddTournamentContent(
        modifier = Modifier.then(modifier),
        state = state,
        tournamentNameState = viewModel.tournamentNameState,
        onEvent = { event -> viewModel.onEvent(event) },
        snackbarHostState = snackbarHostState,
        onBack = { navController.navigateUp() },
        onNavigateToSettings = { navController.navigate(SettingsFlowRoute) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentContent(
    modifier: Modifier = Modifier,
    state: AddTournamentState,
    tournamentNameState: TextFieldState,
    onEvent: (AddTournamentUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val tournamentType = state.tournamentType
    var isPreviewDialogOpen by remember { mutableStateOf(false) }

    val regularSetTemplate =
        (state.regularSetComponentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate
    val decidingSetTemplate =
        (state.decidingSetComponentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate
    val selectedTheme =
        (state.themeComponentState.selectedTheme as? ThemeComponentState.SelectedThemeState.Idle)?.theme

    Scaffold(
        modifier = modifier,
        topBar = {
            AddTournamentAppBar(
                onBack = onBack,
                onNavigateToSettings = onNavigateToSettings,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            // Tournament name + Tournament type
            TournamentNameAndTypeComponent(
                tournamentNameState = tournamentNameState,
                currentTournamentType = tournamentType,
                onTournamentTypeChange = { type ->
                    onEvent(AddTournamentUiEvent.SelectTournamentType(type))
                },
            )

            // Scoring and theme settings block
            MatchScoringAndThemeSettingsBlock(
                setsToWin = state.setsToWin,
                regularSetComponentState = state.regularSetComponentState,
                decidingSetComponentState = state.decidingSetComponentState,
                themeComponentState = state.themeComponentState,
                onSetsToWinChange = { delta ->
                    onEvent(AddTournamentUiEvent.ChangeSetsToWin(delta))
                },
                onSetTemplatesFetch = { filter ->
                    onEvent(AddTournamentUiEvent.FetchSetTemplates(filter))
                },
                onRegularSetTemplateChange = { template ->
                    onEvent(AddTournamentUiEvent.SelectRegularSetTemplate(template))
                },
                onDecidingSetTemplateChange = { template ->
                    onEvent(AddTournamentUiEvent.SelectDecidingSetTemplate(template))
                },
                onThemeSelected = { theme ->
                    onEvent(AddTournamentUiEvent.SelectTheme(theme))
                },
                onThemesFetch = {
                    onEvent(AddTournamentUiEvent.FetchThemes)
                },
                onPreviewClick = { isPreviewDialogOpen = true },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            val needsRegularSet = state.setsToWin > 1
            val isButtonEnabled =
                state.tournamentType != null
                        && tournamentNameState.text.toString().isNotBlank()
                        && decidingSetTemplate != null
                        && selectedTheme != null
                        && (!needsRegularSet || regularSetTemplate != null)
                        && !state.isAdding

            Button(
                onClick = {
                    onEvent(
                        AddTournamentUiEvent.AddTournament(
                            tournamentName = tournamentNameState.text.toString().trim(),
                            tournamentType = tournamentType!!,
                            defaultSetTemplateId = regularSetTemplate?.id,
                            decidingSetTemplateId = decidingSetTemplate!!.id,
                            themeId = selectedTheme!!.id,
                            setsToWin = state.setsToWin,
                        )
                    )
                },
                enabled = isButtonEnabled
            ) {
                Text(stringResource(Res.string.add))
            }

            if (isPreviewDialogOpen) {
                ScoreboardThemePreviewDialog(
                    onDismissRequest = { isPreviewDialogOpen = false },
                    theme = selectedTheme!!
                )
            }
        }
    }
}
