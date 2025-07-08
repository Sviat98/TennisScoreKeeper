package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.participant.AddMatchParticipantComponent
import com.bashkevich.tennisscorekeeper.components.set_template.SetTemplateCombobox
import com.bashkevich.tennisscorekeeper.components.updateTextField
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
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
        modifier = Modifier.then(modifier).padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstParticipantOptions =
                participantOptions.filter { it.id != secondParticipant.id }
            val secondParticipantOptions =
                participantOptions.filter { it.id != firstParticipant.id }
            AddMatchParticipantComponent(
                modifier = Modifier.weight(1f),
                participantOptions = firstParticipantOptions,
                participant = firstParticipant,
                onParticipantsFetch = { onEvent(AddMatchUiEvent.FetchParticipants) },
                onParticipantChange = { participant ->
                    onEvent(
                        AddMatchUiEvent.SelectParticipant(
                            participantNumber = 1,
                            participant = participant
                        )
                    )
                },
                participantPrimaryColor = firstParticipant.primaryColor,
                participantSecondaryColor = firstParticipant.secondaryColor,
                onColorPickerOpen = { colorNumber ->
                    onEvent(
                        AddMatchUiEvent.OpenColorPickerDialog(
                            participantNumber = 1,
                            colorNumber = colorNumber
                        )
                    )
                },
                onToggleSecondaryColor = { color ->
                    onEvent(
                        AddMatchUiEvent.SelectSecondaryColor(
                            participantNumber = 1,
                            color = color
                        )
                    )
                }
            )

            AddMatchParticipantComponent(
                modifier = Modifier.weight(1f),
                participantOptions = secondParticipantOptions,
                participant = secondParticipant,
                onParticipantsFetch = { onEvent(AddMatchUiEvent.FetchParticipants) },
                onParticipantChange = { participant ->
                    onEvent(
                        AddMatchUiEvent.SelectParticipant(
                            participantNumber = 2,
                            participant = participant
                        )
                    )
                },
                participantPrimaryColor = secondParticipant.primaryColor,
                participantSecondaryColor = secondParticipant.secondaryColor,
                onColorPickerOpen = { colorNumber ->
                    onEvent(
                        AddMatchUiEvent.OpenColorPickerDialog(
                            participantNumber = 2,
                            colorNumber = colorNumber
                        )
                    )
                },
                onToggleSecondaryColor = { color ->
                    onEvent(
                        AddMatchUiEvent.SelectSecondaryColor(
                            participantNumber = 2,
                            color = color
                        )
                    )
                }
            )
        }

        val setsToWin = state.setsToWin
        Row {
            Button(
                onClick = { onEvent(AddMatchUiEvent.ChangeSetsToWin(-1)) },
                enabled = setsToWin > 1
            ) {
                Text(" - ")
            }
            Text(setsToWin.toString())
            Button(
                onClick = { onEvent(AddMatchUiEvent.ChangeSetsToWin(1)) },
                enabled = setsToWin < 4
            ) {
                Text(" + ")
            }
        }
        val setTemplateOptions = state.setTemplateOptions

        val regularSetTemplate = state.regularSetTemplate
        val decidingSetTemplate = state.decidingSetTemplate


        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            val regularSetTemplateOptions = setTemplateOptions.filter { it.isRegular }
            val decidingSetTemplateOptions = setTemplateOptions.filter { it.isDeciding }

            SetTemplateCombobox(
                setTemplateOptions = regularSetTemplateOptions,
                enabled = setsToWin > 1,
                currentSetTemplate = regularSetTemplate,
                onSetTemplatesFetch = {
                    onEvent(
                        AddMatchUiEvent.FetchSetTemplates(
                            SetTemplateTypeFilter.ALL
                        )
                    )
                },
                onSetTemplateChange = { setTemplate ->
                    onEvent(
                        AddMatchUiEvent.SelectSetTemplate(
                            SetTemplateTypeFilter.REGULAR,
                            setTemplate
                        )
                    )
                })
            SetTemplateCombobox(
                setTemplateOptions = decidingSetTemplateOptions,
                enabled = true,
                currentSetTemplate = decidingSetTemplate,
                onSetTemplatesFetch = {
                    onEvent(
                        AddMatchUiEvent.FetchSetTemplates(
                            SetTemplateTypeFilter.ALL
                        )
                    )
                },
                onSetTemplateChange = { setTemplate ->
                    onEvent(
                        AddMatchUiEvent.SelectSetTemplate(
                            SetTemplateTypeFilter.DECIDER,
                            setTemplate
                        )
                    )
                })
        }

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

@Composable
fun ParticipantDisplayNameComponent(
    participant: TennisParticipantInMatch
) {
    val participantDisplayName = participant.displayName
    if (participant is ParticipantInDoublesMatch) {
        val (firstPlayerDisplayName, secondPlayerDisplayName) = if (participantDisplayName.isNotEmpty()) {
            participantDisplayName.split('/')
        } else {
            listOf("", "")
        }

        val firstPlayerDisplayNameState = rememberTextFieldState(firstPlayerDisplayName)
        val secondPlayerDisplayNameState = rememberTextFieldState(secondPlayerDisplayName)

        LaunchedEffect(firstPlayerDisplayName) {
            firstPlayerDisplayNameState.updateTextField(firstPlayerDisplayName)
        }

        LaunchedEffect(secondPlayerDisplayName) {
            secondPlayerDisplayNameState.updateTextField(secondPlayerDisplayName)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerDisplayNameTextField(
                state = firstPlayerDisplayNameState,
            )
            Text("/")
            PlayerDisplayNameTextField(
                state = secondPlayerDisplayNameState,
            )
        }
    } else {
        val playerDisplayNameState = rememberTextFieldState(participantDisplayName)

        LaunchedEffect(participantDisplayName) {
            playerDisplayNameState.updateTextField(participantDisplayName)
        }
        PlayerDisplayNameTextField(
            state = playerDisplayNameState,
        )
    }
}

@Composable
fun PlayerDisplayNameTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState
) {
    TextField(
        modifier = Modifier.then(modifier).width(200.dp),
        state = state,
        inputTransformation = UppercaseVisualTransformation
    )
}

object UppercaseVisualTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        // Получаем текущее содержимое буфера
        val currentText = this.asCharSequence()

        // Проверяем, отличается ли текущий текст от его версии в верхнем регистре
        if (currentText.any { it.isLowerCase() }) { // Проверяем, есть ли хотя бы один символ в нижнем регистре
            // Если да, преобразуем весь буфер в верхний регистр
            val transformedText = currentText.map { it.uppercaseChar() }.joinToString("")

            // Заменяем содержимое буфера на преобразованный текст
            replace(0, length, transformedText)
        }
    }
}