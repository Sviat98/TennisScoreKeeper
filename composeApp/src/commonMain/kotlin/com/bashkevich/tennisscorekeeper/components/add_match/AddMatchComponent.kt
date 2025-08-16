package com.bashkevich.tennisscorekeeper.components.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.add_match.participant.AddMatchParticipantsBlock
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchState
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchUiEvent
import com.bashkevich.tennisscorekeeper.screens.addmatch.OpenColorPickerDialogState

@Composable
fun AddMatchComponent(
    modifier: Modifier = Modifier,
    state: AddMatchState,
    onEvent: (AddMatchUiEvent) -> Unit,
    onNavigateAfterMatchAdd: () -> Unit
) {
    val participantOptions = state.participantOptions
    val firstParticipant = state.firstParticipant
    val secondParticipant = state.secondParticipant


    Column(
        modifier = Modifier.then(modifier).padding(16.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddMatchParticipantsBlock(
            modifier = Modifier.fillMaxWidth(),
            participantOptions = participantOptions,
            firstParticipant = firstParticipant,
            secondParticipant = secondParticipant,
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

        val setsToWin = state.setsToWin

        val setTemplateOptions = state.setTemplateOptions

        val regularSetTemplate = state.regularSetTemplate
        val decidingSetTemplate = state.decidingSetTemplate

        SetsToWinBlock(
            modifier = Modifier.fillMaxWidth(),
            setsToWin = setsToWin,
            onValueChange = { delta -> onEvent(AddMatchUiEvent.ChangeSetsToWin(delta)) }
        )

        MatchScoringSettingsBlock(
            modifier = Modifier.fillMaxWidth(),
            setsToWin = setsToWin,
            setTemplateOptions = setTemplateOptions,
            regularSetTemplate = regularSetTemplate,
            decidingSetTemplate = decidingSetTemplate,
            onSetTemplatesFetch = {
                onEvent(
                    AddMatchUiEvent.FetchSetTemplates(
                        SetTemplateTypeFilter.ALL
                    )
                )
            },
            onSetTemplateChange = { setTemplateType, setTemplate ->
                onEvent(
                    AddMatchUiEvent.SelectSetTemplate(
                        setTemplateTypeFilter = setTemplateType,
                        setTemplate = setTemplate
                    )
                )
            }
        )

        Button(
            onClick = {
                onEvent(AddMatchUiEvent.AddMatch)
                onNavigateAfterMatchAdd()
            }
        ) {
            Text("Add Match")
        }

        val dialogState = state.dialogState
        if (dialogState is OpenColorPickerDialogState.OpenColorPicker) {

            val colorNumber = dialogState.colorNumber
            val participantNumber = dialogState.participantNumber

            val initialColor = if (colorNumber == 1) {
                if (participantNumber == 1) {
                    state.firstParticipant.primaryColor
                } else {
                    state.secondParticipant.primaryColor
                }
            } else {
                if (participantNumber == 1) {
                    state.firstParticipant.secondaryColor!!
                } else {
                    state.secondParticipant.secondaryColor!!
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
    }
}

