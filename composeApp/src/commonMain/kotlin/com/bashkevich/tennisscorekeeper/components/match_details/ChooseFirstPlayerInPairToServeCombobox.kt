package com.bashkevich.tennisscorekeeper.components.match_details

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
import com.bashkevich.tennisscorekeeper.model.player.domain.TennisPlayerInMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.toDisplayFormat

@Composable
fun ChooseFirstPlayerInPairToServeCombobox(
    modifier: Modifier = Modifier,
    playerOptions: List<TennisPlayerInMatch>,
    currentPlayer: TennisPlayerInMatch,
    onPlayerChange: (TennisPlayerInMatch) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val playerText = currentPlayer.toDisplayFormat()

    val playerState = rememberTextFieldState(playerText)

    LaunchedEffect(playerText){
        playerState.updateTextField(playerText)
    }

    Box {
        // Поле ввода с заблокированным редактированием
        TextField(
            modifier = Modifier.then(modifier),
            state = playerState,
            placeholder = { Text("Player") },
            readOnly = true,
            enabled = enabled,
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
            playerOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onPlayerChange(option)
                        expanded = false
                    }
                ) {
                    Text(text = option.toDisplayFormat())
                }
            }
        }
    }
}