package com.bashkevich.tennisscorekeeper.components.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.updateTextField
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ParticipantDisplayNameComponent(
    modifier: Modifier = Modifier,
    participant: TennisParticipantInMatch,
    onParticipantDisplayNameChange: (String) -> Unit
) {
    val participantDisplayName = participant.displayName

    Box(modifier = Modifier.then(modifier)) {
        if (participant is ParticipantInDoublesMatch) {
            val (firstPlayerDisplayName, secondPlayerDisplayName) = if (participantDisplayName.isNotEmpty()) {
                participantDisplayName.split('/')
            } else {
                listOf("", "")
            }

            val firstPlayerDisplayNameState = rememberTextFieldState(firstPlayerDisplayName)
            val secondPlayerDisplayNameState = rememberTextFieldState(secondPlayerDisplayName)

            LaunchedEffect(participant) {
                firstPlayerDisplayNameState.updateTextField(firstPlayerDisplayName)
                secondPlayerDisplayNameState.updateTextField(secondPlayerDisplayName)
            }

            LaunchedEffect(firstPlayerDisplayNameState, secondPlayerDisplayNameState) {
                snapshotFlow { firstPlayerDisplayNameState.text.trim().toString() to secondPlayerDisplayNameState.text.trim().toString() }.collectLatest { (firstPlayerDisplayName, secondPlayerDisplayName) ->
                    val displayName = "$firstPlayerDisplayName/$secondPlayerDisplayName"

                    println("final displayName = $displayName")

                    onParticipantDisplayNameChange(displayName)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerDisplayNameTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = firstPlayerDisplayNameState,
                )
                Text("/")
                PlayerDisplayNameTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = secondPlayerDisplayNameState,
                )
            }
        } else {
            val playerDisplayNameState = rememberTextFieldState(participantDisplayName)

            LaunchedEffect(participant) {
                playerDisplayNameState.updateTextField(participantDisplayName)
            }

            LaunchedEffect(playerDisplayNameState) {
                snapshotFlow { playerDisplayNameState.text.trim().toString() }.collect { displayName ->
                    onParticipantDisplayNameChange(displayName)
                }
            }

            PlayerDisplayNameTextField(
                modifier = Modifier.fillMaxWidth(),
                state = playerDisplayNameState,
            )
        }
    }

}

@Composable
fun PlayerDisplayNameTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState
) {
    TextField(
        modifier = Modifier.then(modifier),
        state = state,
        inputTransformation = PlayerDisplayNameVisualTransformation
    )
}

object PlayerDisplayNameVisualTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        // Получаем текущее содержимое буфера
        val currentText = this.asCharSequence()

        var transformedText = currentText

        // Проверяем, отличается ли текущий текст от его версии в верхнем регистре
        if (currentText.any { it.isLowerCase() }) { // Проверяем, есть ли хотя бы один символ в нижнем регистре
            // Если да, преобразуем весь буфер в верхний регистр
            transformedText = currentText.map { it.uppercaseChar() }.joinToString("")
        }

        transformedText = transformedText.filter { it != '/' }

        // Заменяем содержимое буфера на преобразованный текст
        replace(0, length, transformedText)
    }
}