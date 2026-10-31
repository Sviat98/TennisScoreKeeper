package com.bashkevich.tennisscorekeeper.screens.editmatch

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.bashkevich.tennisscorekeeper.components.ComponentMode
import com.bashkevich.tennisscorekeeper.components.add_match.TournamentAndMatchSettingsBlock
import com.bashkevich.tennisscorekeeper.components.add_match.participant.AddMatchParticipantsBlock
import com.bashkevich.tennisscorekeeper.components.dialog.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Check
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.screens.addmatch.OpenColorPickerDialogState
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

    val match = state.match
    val editedMatch = state.editedMatch

    // editedMatch структурно расходится с матчем только когда есть пользовательские правки;
    // возврат к исходным значениям (тот же цвет/тема/имя) снова даёт равенство
    val hasUnsavedChanges = match != null && editedMatch != null && editedMatch != match

    // COMPLETED — невозвратное состояние: экран открыт на просмотр, все поля залочены
    val isCompleted = match?.status == MatchStatus.COMPLETED

    val isSaveEnabled = match != null &&
            match.status != MatchStatus.COMPLETED &&
            editedMatch != null &&
            editedMatch.firstParticipant.displayName.isNotBlank() &&
            editedMatch.secondParticipant.displayName.isNotBlank() &&
            !state.isSaving

    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = {
            EditMatchTopAppBar(
                isSaveVisible = hasUnsavedChanges && !isCompleted,
                isSaveEnabled = isSaveEnabled,
                onBack = { navController.navigateUp() },
                onSave = { viewModel.onEvent(EditMatchUiEvent.SaveMatch) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
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
    isSaveVisible: Boolean,
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
private fun EditMatchContent(
    modifier: Modifier = Modifier,
    state: EditMatchState,
    editedMatch: Match,
    onEvent: (EditMatchUiEvent) -> Unit,
) {
    // COMPLETED — невозвратное состояние: все поля залочены, экран открыт на просмотр
    val isCompleted = editedMatch.status == MatchStatus.COMPLETED

    Box(
        modifier = Modifier.then(modifier)
            .fillMaxWidth()
            .padding(all = 16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchDetailsScoreboardView(
                modifier = Modifier.horizontalScroll(state = rememberScrollState()),
                match = editedMatch,
                theme = state.theme,
            )

            // Выбор участника залочен (ComponentMode.EDIT), редактируются
            // display-имена и цвета; адаптивность — как на экране добавления матча.
            // У COMPLETED-матча залочено всё, включая display-имена и цвета
            AddMatchParticipantsBlock(
                modifier = Modifier.fillMaxWidth(),
                participantOptions = emptyList(),
                firstParticipant = editedMatch.firstParticipant,
                secondParticipant = editedMatch.secondParticipant,
                mode = ComponentMode.EDIT,
                canEditParticipants = !isCompleted,
                onParticipantsFetch = {},
                onParticipantChange = { _, _ -> },
                onParticipantDisplayNameChange = { participantNumber, displayName ->
                    onEvent(EditMatchUiEvent.ChangeDisplayName(participantNumber, displayName))
                },
                onColorPickerOpen = { participantNumber, colorNumber ->
                    onEvent(
                        EditMatchUiEvent.OpenColorPickerDialog(
                            participantNumber = participantNumber,
                            colorNumber = colorNumber
                        )
                    )
                },
                onToggleSecondaryColor = { participantNumber, color ->
                    onEvent(
                        EditMatchUiEvent.SelectSecondaryColor(
                            participantNumber = participantNumber,
                            color = color
                        )
                    )
                }
            )

            // Тема + залоченные настройки счёта (sets_to_win, шаблоны сетов) — нижняя часть
            // как на экране добавления матча. На широком экране блок повторяет геометрию
            // блока участников (Row до 1000dp с weight-колонками), поэтому тема остаётся
            // под первым участником; настройки счёта приходят в самом матче и не редактируются.
            // У COMPLETED-матча залочена и тема
            TournamentAndMatchSettingsBlock(
                modifier = Modifier.fillMaxWidth(),
                setsToWin = state.setsToWin,
                regularSetComponentState = state.regularSetComponentState,
                decidingSetComponentState = state.decidingSetComponentState,
                themeComponentState = state.themeComponentState,
                canEditMatchScoreSettings = false,
                canEditTheme = !isCompleted,
                onSetsToWinChange = {},
                onSetTemplatesFetch = {},
                onRegularSetTemplateChange = {},
                onDecidingSetTemplateChange = {},
                onThemeSelected = { theme ->
                    onEvent(EditMatchUiEvent.SelectTheme(theme.id))
                },
                onThemesFetch = { onEvent(EditMatchUiEvent.FetchThemes) },
                onRetrySelectedTheme = { themeId ->
                    onEvent(EditMatchUiEvent.RetrySelectedTheme(themeId))
                },
                onRetrySelectedRegularSet = { setId ->
                    onEvent(EditMatchUiEvent.RetrySelectedRegularSet(setId))
                },
                onRetrySelectedDecidingSet = { setId ->
                    onEvent(EditMatchUiEvent.RetrySelectedDecidingSet(setId))
                },
            )
        }
    }

    val dialogState = state.dialogState
    if (dialogState is OpenColorPickerDialogState.OpenColorPicker) {
        val colorNumber = dialogState.colorNumber
        val participantNumber = dialogState.participantNumber

        val initialColor = if (colorNumber == 1) {
            if (participantNumber == 1) {
                editedMatch.firstParticipant.primaryColor
            } else {
                editedMatch.secondParticipant.primaryColor
            }
        } else {
            // Кнопка второго цвета есть только когда secondaryColor != null
            if (participantNumber == 1) {
                editedMatch.firstParticipant.secondaryColor!!
            } else {
                editedMatch.secondParticipant.secondaryColor!!
            }
        }
        ColorPickerDialog(
            initialColor = initialColor,
            onDismissRequest = { onEvent(EditMatchUiEvent.CloseColorPickerDialog) },
            onColorSelected = { color ->
                when (colorNumber) {
                    1 -> onEvent(
                        EditMatchUiEvent.SelectPrimaryColor(
                            participantNumber = participantNumber,
                            color = color
                        )
                    )

                    2 -> onEvent(
                        EditMatchUiEvent.SelectSecondaryColor(
                            participantNumber = participantNumber,
                            color = color
                        )
                    )
                }
            })
    }
}
