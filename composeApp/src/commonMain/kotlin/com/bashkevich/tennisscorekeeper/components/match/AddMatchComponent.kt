package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.participant.ParticipantCombobox
import com.bashkevich.tennisscorekeeper.components.set_template.SetTemplateCombobox
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchState
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchUiEvent
import com.bashkevich.tennisscorekeeper.screens.addmatch.DoublesParticipantDisplayNameState
import com.bashkevich.tennisscorekeeper.screens.addmatch.ParticipantDisplayNameState
import com.bashkevich.tennisscorekeeper.screens.addmatch.SinglesParticipantDisplayNameState

@Composable
fun AddMatchComponent(
    modifier: Modifier = Modifier,
    state: AddMatchState,
    onEvent: (AddMatchUiEvent) -> Unit,
    onNavigateAfterMatchAdd: ()-> Unit
) {
    val participantOptions = state.participantOptions
    val firstParticipant = state.firstParticipant
    val secondParticipant = state.secondParticipant

    val firstParticipantDisplayNameState = state.firstParticipantDisplayName
    val secondParticipantDisplayNameState = state.secondParticipantDisplayName

    Column(
        modifier = Modifier.then(modifier)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val firstParticipantOptions = participantOptions.filter { it != secondParticipant }
            val secondParticipantOptions = participantOptions.filter { it != firstParticipant }


            ParticipantCombobox(
                participantOptions = firstParticipantOptions,
                currentParticipant = firstParticipant,
                onParticipantsFetch = { onEvent(AddMatchUiEvent.FetchParticipants) },
                onParticipantChange = { participant ->
                    onEvent(
                        AddMatchUiEvent.SelectParticipant(
                            participantNumber = 1,
                            participant = participant
                        )
                    )
                })
            ParticipantCombobox(
                participantOptions = secondParticipantOptions,
                currentParticipant = secondParticipant,
                onParticipantsFetch = { onEvent(AddMatchUiEvent.FetchParticipants) },
                onParticipantChange = { participant ->
                    onEvent(
                        AddMatchUiEvent.SelectParticipant(
                            participantNumber = 2,
                            participant = participant
                        )
                    )
                })
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            ParticipantDisplayNameComponent(
                participantDisplayNameState = firstParticipantDisplayNameState
            )
            ParticipantDisplayNameComponent(
                participantDisplayNameState = secondParticipantDisplayNameState
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
    }
}

@Composable
fun ParticipantDisplayNameComponent(
    participantDisplayNameState: ParticipantDisplayNameState
) {
    when (participantDisplayNameState) {
        is SinglesParticipantDisplayNameState -> {
            TextField(
                state = participantDisplayNameState.playerDisplayName,
                inputTransformation = UppercaseVisualTransformation
            )
        }

        is DoublesParticipantDisplayNameState -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    state = participantDisplayNameState.firstPlayerDisplayName,
                    inputTransformation = UppercaseVisualTransformation
                )
                Text("/")
                TextField(
                    state = participantDisplayNameState.secondPlayerDisplayName,
                    inputTransformation = UppercaseVisualTransformation
                )
            }
        }
    }
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