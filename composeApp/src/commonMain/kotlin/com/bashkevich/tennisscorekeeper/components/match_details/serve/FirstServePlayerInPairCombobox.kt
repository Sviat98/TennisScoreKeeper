package com.bashkevich.tennisscorekeeper.components.match_details.serve

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
import com.bashkevich.tennisscorekeeper.model.player.domain.TennisPlayerInMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.toDisplayFormat

@Composable
fun FirstServePlayerInPairCombobox(
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
                    imageVector = IconGroup.Default.ArrowDropDown,
                    contentDescription = "Open dropdown",
                    modifier = Modifier.clickable {
                        expanded = true
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
            playerOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.toDisplayFormat()) },
                    onClick = {
                        onPlayerChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}