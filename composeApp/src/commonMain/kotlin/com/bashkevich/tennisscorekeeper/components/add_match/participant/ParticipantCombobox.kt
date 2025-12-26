package com.bashkevich.tennisscorekeeper.components.add_match.participant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.updateTextField
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDisplayFormat

@Composable
fun ParticipantCombobox(
    modifier: Modifier = Modifier,
    participantOptions: List<TennisParticipant>,
    currentParticipant: TennisParticipantInMatch,
    onParticipantsFetch: ()-> Unit,
    onParticipantChange: (TennisParticipant) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val participantText = currentParticipant.toDisplayFormat()

    val participantState = rememberTextFieldState(participantText)

    LaunchedEffect(participantText){
        println(participantText)
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
                    imageVector = IconGroup.Default.ArrowDropDown,
                    contentDescription = "Open dropdown",
                    modifier = Modifier.clickable {
                        expanded = true
                        onParticipantsFetch()
                    }
                )
            },
            colors = TextFieldDefaults.colors(
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
                    text = { Text(text = option.toDisplayFormat()) },
                    onClick = {
                        onParticipantChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}