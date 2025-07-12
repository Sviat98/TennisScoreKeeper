package com.bashkevich.tennisscorekeeper.components.participant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.updateTextField
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDisplayFormat

@Composable
fun FirstServeParticipantCombobox(
    modifier: Modifier = Modifier,
    participantOptions: List<TennisParticipantInMatch>,
    currentParticipant: TennisParticipantInMatch,
    onParticipantChange: (TennisParticipantInMatch) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val participantText = currentParticipant.toDisplayFormat()

    val participantState = rememberTextFieldState(participantText)

    LaunchedEffect(participantText){
        participantState.updateTextField(participantText)
    }

    Box {
        // Поле ввода с заблокированным редактированием
        TextField(
            modifier = Modifier.then(modifier),
            state = participantState,
            placeholder = { Text("Participant") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open dropdown",
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            )
        )

        // Выпадающее меню
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            participantOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onParticipantChange(option)
                        expanded = false
                    }
                ) {
                    Text(text = option.toDisplayFormat())
                }
            }
        }
    }
}