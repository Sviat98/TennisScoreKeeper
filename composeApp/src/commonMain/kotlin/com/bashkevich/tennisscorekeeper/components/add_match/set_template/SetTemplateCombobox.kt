package com.bashkevich.tennisscorekeeper.components.add_match.set_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Composable
fun SetTemplateCombobox(
    modifier: Modifier = Modifier,
    setTemplateOptions: List<SetTemplate>,
    enabled: Boolean,
    currentSetTemplate: SetTemplate,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val setTemplateText = currentSetTemplate.name

    val setTemplateState = TextFieldState(setTemplateText)

    Box(
        modifier = Modifier.then(modifier)
    ) {
        // Поле ввода с заблокированным редактированием
        TextField(
            modifier = Modifier.fillMaxWidth(),
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
                        imageVector = IconGroup.Default.ArrowDropDown,
                        contentDescription = "Open dropdown",
                    )
                }
            },
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2, maxHeightInLines = 3),
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
            setTemplateOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.name) },
                    onClick = {
                        onSetTemplateChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}