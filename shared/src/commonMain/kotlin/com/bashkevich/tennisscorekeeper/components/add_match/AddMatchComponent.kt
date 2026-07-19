package com.bashkevich.tennisscorekeeper.components.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.add_match.participant.AddMatchParticipantsBlock
import com.bashkevich.tennisscorekeeper.components.dialog.ScoreboardThemePreviewDialog
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchLoadingState
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchUiEvent
import com.bashkevich.tennisscorekeeper.screens.addmatch.OpenColorPickerDialogState
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.add

@Composable
fun AddMatchComponent(
    modifier: Modifier = Modifier,
    contentState: AddMatchLoadingState.Content,
    onEvent: (AddMatchUiEvent) -> Unit,
) {
    val participantState = contentState.participantComponentState
    var isPreviewDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.then(modifier).padding(16.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddMatchParticipantsBlock(
            modifier = Modifier.fillMaxWidth(),
            participantOptions = participantState.options,
            firstParticipant = participantState.firstParticipant,
            secondParticipant = participantState.secondParticipant,
            onParticipantsFetch = { onEvent(AddMatchUiEvent.FetchParticipants) },
            onParticipantChange = { participantNumber, participant ->
                onEvent(
                    AddMatchUiEvent.SelectParticipant(
                        participantNumber = participantNumber,
                        participant = participant
                    )
                )
            },
            onParticipantDisplayNameChange = { participantNumber, displayName ->
                onEvent(
                    AddMatchUiEvent.ChangeDisplayName(
                        participantNumber = participantNumber,
                        displayName = displayName
                    )
                )
            },
            onColorPickerOpen = { participantNumber, colorNumber ->
                onEvent(
                    AddMatchUiEvent.OpenColorPickerDialog(
                        participantNumber = participantNumber,
                        colorNumber = colorNumber
                    )
                )
            },
            onToggleSecondaryColor = { participantNumber, color ->
                onEvent(
                    AddMatchUiEvent.SelectSecondaryColor(
                        participantNumber = participantNumber,
                        color = color
                    )
                )
            }
        )

        MatchScoringAndThemeSettingsBlock(
            setsToWin = contentState.setsToWin,
            regularSetComponentState = contentState.regularSetComponentState,
            decidingSetComponentState = contentState.decidingSetComponentState,
            themeComponentState = contentState.themeComponentState,
            onSetsToWinChange = { delta -> onEvent(AddMatchUiEvent.ChangeSetsToWin(delta)) },
            onSetTemplatesFetch = { filter ->
                onEvent(AddMatchUiEvent.FetchSetTemplates(filter))
            },
            onRegularSetTemplateChange = { setTemplate ->
                onEvent(
                    AddMatchUiEvent.SelectSetTemplate(
                        setTemplateTypeFilter = SetTemplateTypeFilter.REGULAR,
                        setTemplate = setTemplate
                    )
                )
            },
            onDecidingSetTemplateChange = { setTemplate ->
                onEvent(
                    AddMatchUiEvent.SelectSetTemplate(
                        setTemplateTypeFilter = SetTemplateTypeFilter.DECIDER,
                        setTemplate = setTemplate
                    )
                )
            },
            onThemeSelected = { theme ->
                onEvent(AddMatchUiEvent.SelectTheme(theme))
            },
            onThemesFetch = {
                onEvent(AddMatchUiEvent.FetchThemes)
            },
            onRetrySelectedTheme = { themeId ->
                onEvent(AddMatchUiEvent.RetrySelectedTheme(themeId))
            },
            onRetrySelectedRegularSet = { setId ->
                onEvent(AddMatchUiEvent.RetrySelectedRegularSet(setId))
            },
            onRetrySelectedDecidingSet = { setId ->
                onEvent(AddMatchUiEvent.RetrySelectedDecidingSet(setId))
            },
            onPreviewClick = { isPreviewDialogOpen = true },
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Add button
        val regularSetTemplate = contentState.regularSetComponentState.selectedSetState.let {
            (it as? SetComponentState.SelectedSetState.Idle)?.setTemplate
        }
        val decidingSetTemplate = contentState.decidingSetComponentState.selectedSetState.let {
            (it as? SetComponentState.SelectedSetState.Idle)?.setTemplate
        }
        val selectedTheme = contentState.themeComponentState.selectedTheme.let {
            (it as? ThemeComponentState.SelectedThemeState.Idle)?.theme
        }

        val needsRegularSet = contentState.setsToWin > 1
        val isButtonEnabled =
            participantState.firstParticipant.id != 0
                    && participantState.secondParticipant.id != 0
                    && participantState.firstParticipant.id != participantState.secondParticipant.id
                    && decidingSetTemplate != null
                    && selectedTheme != null
                    && (!needsRegularSet || regularSetTemplate != null)
                    && !contentState.isAdding

        Button(
            onClick = {
                onEvent(AddMatchUiEvent.AddMatch)
            },
            enabled = isButtonEnabled
        ) {
            if (contentState.isAdding) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(Res.string.add))
            }
        }

        val dialogState = contentState.dialogState
        if (dialogState is OpenColorPickerDialogState.OpenColorPicker) {
            val colorNumber = dialogState.colorNumber
            val participantNumber = dialogState.participantNumber

            val initialColor = if (colorNumber == 1) {
                if (participantNumber == 1) {
                    participantState.firstParticipant.primaryColor
                } else {
                    participantState.secondParticipant.primaryColor
                }
            } else {
                if (participantNumber == 1) {
                    participantState.firstParticipant.secondaryColor!!
                } else {
                    participantState.secondParticipant.secondaryColor!!
                }
            }
            ColorPickerDialog(
                initialColor = initialColor,
                onDismissRequest = { onEvent(AddMatchUiEvent.CloseColorPickerDialog) },
                onColorSelected = { color ->
                    when (colorNumber) {
                        1 -> onEvent(
                            AddMatchUiEvent.SelectPrimaryColor(
                                participantNumber = participantNumber,
                                color = color
                            )
                        )
                        2 -> onEvent(
                            AddMatchUiEvent.SelectSecondaryColor(
                                participantNumber = participantNumber,
                                color = color
                            )
                        )
                    }
                })
        }

        if (isPreviewDialogOpen) {
            ScoreboardThemePreviewDialog(
                onDismissRequest = { isPreviewDialogOpen = false },
                theme = selectedTheme!!
            )
        }
    }
}
