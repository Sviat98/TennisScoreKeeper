package com.bashkevich.tennisscorekeeper.screens.edittournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.add_match.TournamentAndMatchSettingsBlock
import com.bashkevich.tennisscorekeeper.components.add_tournament.TournamentNameAndTypeComponent
import com.bashkevich.tennisscorekeeper.components.dialog.ScoreboardThemePreviewDialog
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Check
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.edit_tournament
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.save

@Composable
fun EditTournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: EditTournamentViewModel,
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
            is EditTournamentAction.TournamentSaved -> navController.navigateUp()

            is EditTournamentAction.ShowUnauthorizedError ->
                snackbarHostState.showUnauthorizedActionSnackbar(navController = navController)

            is EditTournamentAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    val tournament = state.tournament
    val editedTournament = state.editedTournament

    // Название живёт в TextFieldState экрана (пересоздаётся, когда турнир загрузился из БД)
    val tournamentNameState = remember(editedTournament?.id) {
        TextFieldState(editedTournament?.name ?: "")
    }
    val editedName = tournamentNameState.text.toString().trim()

    // editedTournament структурно расходится с турниром только когда есть пользовательские правки
    // (настройки счёта/тема); название сравнивается отдельно, потому что живёт в TextFieldState
    val hasUnsavedChanges = tournament != null && editedTournament != null &&
            (editedTournament != tournament || editedName != tournament.name)

    // COMPLETED — невозвратное состояние: экран открыт на просмотр, все поля залочены
    val isCompleted = tournament?.status == TournamentStatus.COMPLETED

    val regularSetTemplate =
        (state.regularSetComponentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate
    val decidingSetTemplate =
        (state.decidingSetComponentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate
    val selectedTheme =
        (state.themeComponentState.selectedTheme as? ThemeComponentState.SelectedThemeState.Idle)?.theme

    val needsRegularSet = state.setsToWin > 1
    val isSaveEnabled = tournament != null && editedTournament != null &&
            editedName.isNotBlank() &&
            decidingSetTemplate != null &&
            selectedTheme != null &&
            (!needsRegularSet || regularSetTemplate != null) &&
            !state.isSaving

    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = {
            EditTournamentTopAppBar(
                isSaveVisible = hasUnsavedChanges && !isCompleted,
                isSaveEnabled = isSaveEnabled,
                onBack = { navController.navigateUp() },
                onSave = {
                    viewModel.onEvent(EditTournamentUiEvent.SaveTournament(tournamentName = editedName))
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (editedTournament == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EditTournamentContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                state = state,
                editedTournament = editedTournament,
                tournamentNameState = tournamentNameState,
                onEvent = { viewModel.onEvent(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTournamentTopAppBar(
    isSaveVisible: Boolean,
    isSaveEnabled: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.edit_tournament)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
                )
            }
        },
        actions = {
            if (isSaveVisible) {
                IconButton(onClick = onSave, enabled = isSaveEnabled) {
                    Icon(
                        imageVector = IconGroup.Default.Check,
                        contentDescription = stringResource(Res.string.save)
                    )
                }
            }
        }
    )
}

@Composable
private fun EditTournamentContent(
    modifier: Modifier = Modifier,
    state: EditTournamentState,
    editedTournament: Tournament,
    tournamentNameState: TextFieldState,
    onEvent: (EditTournamentUiEvent) -> Unit,
) {
    // COMPLETED — невозвратное состояние: все поля залочены, экран открыт на просмотр
    val isCompleted = editedTournament.status == TournamentStatus.COMPLETED

    var isPreviewDialogOpen by remember { mutableStateOf(false) }

    val selectedTheme =
        (state.themeComponentState.selectedTheme as? ThemeComponentState.SelectedThemeState.Idle)?.theme

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Название редактируется; тип залочен всегда — турнир не меняет тип;
        // у COMPLETED-турнира залочено и название
        TournamentNameAndTypeComponent(
            tournamentNameState = tournamentNameState,
            canEditName = !isCompleted,
            currentTournamentType = editedTournament.type,
            canEditType = false,
            onTournamentTypeChange = { },
        )

        // Настройки счёта (sets_to_win + шаблоны сетов) и тема редактируются,
        // как на экране добавления турнира; у COMPLETED-турнира залочено всё
        TournamentAndMatchSettingsBlock(
            modifier = Modifier.fillMaxWidth(),
            setsToWin = state.setsToWin,
            regularSetComponentState = state.regularSetComponentState,
            decidingSetComponentState = state.decidingSetComponentState,
            themeComponentState = state.themeComponentState,
            canEditMatchScoreSettings = !isCompleted,
            canEditTheme = !isCompleted,
            onSetsToWinChange = { delta ->
                onEvent(EditTournamentUiEvent.ChangeSetsToWin(delta))
            },
            onSetTemplatesFetch = { filter ->
                onEvent(EditTournamentUiEvent.FetchSetTemplates(filter))
            },
            onRegularSetTemplateChange = { template ->
                onEvent(EditTournamentUiEvent.SelectRegularSetTemplate(template))
            },
            onDecidingSetTemplateChange = { template ->
                onEvent(EditTournamentUiEvent.SelectDecidingSetTemplate(template))
            },
            onThemeSelected = { theme ->
                onEvent(EditTournamentUiEvent.SelectTheme(theme.id))
            },
            onThemesFetch = {
                onEvent(EditTournamentUiEvent.FetchThemes)
            },
            onRetrySelectedTheme = { themeId ->
                onEvent(EditTournamentUiEvent.RetrySelectedTheme(themeId))
            },
            onRetrySelectedRegularSet = { setId ->
                onEvent(EditTournamentUiEvent.RetrySelectedRegularSet(setId))
            },
            onRetrySelectedDecidingSet = { setId ->
                onEvent(EditTournamentUiEvent.RetrySelectedDecidingSet(setId))
            },
            onPreviewClick = { isPreviewDialogOpen = true },
        )

        if (isPreviewDialogOpen && selectedTheme != null) {
            ScoreboardThemePreviewDialog(
                modifier = Modifier.fillMaxSize(),
                onDismissRequest = { isPreviewDialogOpen = false },
                theme = selectedTheme
            )
        }
    }
}
