package com.bashkevich.tennisscorekeeper.components.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.components.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.add_match.participant.AddMatchParticipantsBlock
import com.bashkevich.tennisscorekeeper.components.add_match.set_template.SetTemplateComponent
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchState
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchUiEvent
import com.bashkevich.tennisscorekeeper.screens.addmatch.MatchAddingSubstate
import com.bashkevich.tennisscorekeeper.screens.addmatch.OpenColorPickerDialogState
import com.bashkevich.tennisscorekeeper.screens.addtournament.ThemeDropdownMenu

@Composable
fun AddMatchComponent(
    modifier: Modifier = Modifier,
    state: AddMatchState,
    onEvent: (AddMatchUiEvent) -> Unit,
) {
    val participantOptions = state.participantOptions
    val firstParticipant = state.firstParticipant
    val secondParticipant = state.secondParticipant

    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val isWideScreen = windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

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

        val regularTemplates = setTemplateOptions.filter { it.isRegular }
        val decidingTemplates = setTemplateOptions.filter { it.isDeciding }

        // Settings section
        if (isWideScreen) {
            // Row 1: Theme + SetsToWin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(64.dp),
            ) {
                ThemeDropdownMenu(
                    modifier = Modifier.weight(1f),
                    themes = state.themeOptions,
                    selectedTheme = state.selectedTheme,
                    onThemeSelected = { theme ->
                        onEvent(AddMatchUiEvent.SelectTheme(theme))
                    },
                    onThemesFetch = { onEvent(AddMatchUiEvent.FetchThemes) }
                )
                SetsToWinComponent(
                    modifier = Modifier.weight(1f),
                    setsToWin = setsToWin,
                    onValueChange = { delta -> onEvent(AddMatchUiEvent.ChangeSetsToWin(delta)) }
                )
            }

            // Row 2: Regular + Deciding set templates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SetTemplateComponent(
                    modifier = Modifier.weight(1f),
                    label = "Regular Set Template",
                    setTemplateOptions = regularTemplates,
                    enabled = setsToWin > 1,
                    currentSetTemplate = regularSetTemplate,
                    onSetTemplatesFetch = {
                        onEvent(AddMatchUiEvent.FetchSetTemplates(SetTemplateTypeFilter.ALL))
                    },
                    onSetTemplateChange = { setTemplate ->
                        onEvent(
                            AddMatchUiEvent.SelectSetTemplate(
                                setTemplateTypeFilter = SetTemplateTypeFilter.REGULAR,
                                setTemplate = setTemplate
                            )
                        )
                    }
                )
                SetTemplateComponent(
                    modifier = Modifier.weight(1f),
                    label = "Deciding Set Template",
                    setTemplateOptions = decidingTemplates,
                    enabled = true,
                    currentSetTemplate = decidingSetTemplate,
                    onSetTemplatesFetch = {
                        onEvent(AddMatchUiEvent.FetchSetTemplates(SetTemplateTypeFilter.ALL))
                    },
                    onSetTemplateChange = { setTemplate ->
                        onEvent(
                            AddMatchUiEvent.SelectSetTemplate(
                                setTemplateTypeFilter = SetTemplateTypeFilter.DECIDER,
                                setTemplate = setTemplate
                            )
                        )
                    }
                )
            }
        } else {
            // Narrow screen: vertical layout
            ThemeDropdownMenu(
                modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
                themes = state.themeOptions,
                selectedTheme = state.selectedTheme,
                onThemeSelected = { theme ->
                    onEvent(AddMatchUiEvent.SelectTheme(theme))
                },
                onThemesFetch = { onEvent(AddMatchUiEvent.FetchThemes) }
            )

            SetsToWinComponent(
                modifier = Modifier.fillMaxWidth(),
                setsToWin = setsToWin,
                onValueChange = { delta -> onEvent(AddMatchUiEvent.ChangeSetsToWin(delta)) }
            )

            SetTemplateComponent(
                modifier = Modifier.fillMaxWidth(),
                label = "Regular Set Template",
                setTemplateOptions = regularTemplates,
                enabled = setsToWin > 1,
                currentSetTemplate = regularSetTemplate,
                onSetTemplatesFetch = {
                    onEvent(AddMatchUiEvent.FetchSetTemplates(SetTemplateTypeFilter.ALL))
                },
                onSetTemplateChange = { setTemplate ->
                    onEvent(
                        AddMatchUiEvent.SelectSetTemplate(
                            setTemplateTypeFilter = SetTemplateTypeFilter.REGULAR,
                            setTemplate = setTemplate
                        )
                    )
                }
            )

            SetTemplateComponent(
                modifier = Modifier.fillMaxWidth(),
                label = "Deciding Set Template",
                setTemplateOptions = decidingTemplates,
                enabled = true,
                currentSetTemplate = decidingSetTemplate,
                onSetTemplatesFetch = {
                    onEvent(AddMatchUiEvent.FetchSetTemplates(SetTemplateTypeFilter.ALL))
                },
                onSetTemplateChange = { setTemplate ->
                    onEvent(
                        AddMatchUiEvent.SelectSetTemplate(
                            setTemplateTypeFilter = SetTemplateTypeFilter.DECIDER,
                            setTemplate = setTemplate
                        )
                    )
                }
            )
        }

        val matchAddingSubstate = state.matchAddingSubstate

        val isButtonEnabled = matchAddingSubstate !is MatchAddingSubstate.Loading

        Button(
            onClick = {
                onEvent(AddMatchUiEvent.AddMatch)
            },
            enabled = isButtonEnabled
        ) {
            if (matchAddingSubstate is MatchAddingSubstate.Loading){
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }else{
                Text("Add Match")
            }
        }

        if (matchAddingSubstate is MatchAddingSubstate.Error){
            Text(text = matchAddingSubstate.message, color = Color.Red)
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
