package com.bashkevich.tennisscorekeeper.components.set_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Composable
fun SetTemplateCombobox(
    setTemplateOptions: List<SetTemplate>,
    enabled: Boolean,
    currentSetTemplate: SetTemplate,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val setTemplateText = currentSetTemplate.name

    val setTemplateState = TextFieldState(setTemplateText)

    Box {
        // Поле ввода с заблокированным редактированием
        TextField(
            state = setTemplateState,
            placeholder = { Text("Set Template") },
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = true
                        onSetTemplatesFetch()
                    },
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open dropdown",
                    )
                }
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
            setTemplateOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSetTemplateChange(option)
                        expanded = false
                    }
                ) {
                    Text(text = option.name)
                }
            }
        }
    }
}