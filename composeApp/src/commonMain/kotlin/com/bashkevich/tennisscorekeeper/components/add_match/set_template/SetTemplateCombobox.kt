package com.bashkevich.tennisscorekeeper.components.add_match.set_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.screens.addtournament.DropdownLoadState

@Composable
fun SetTemplateCombobox(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    currentSetTemplate: SetTemplate,
    loadState: DropdownLoadState<SetTemplate> = DropdownLoadState.Idle(emptyList()),
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val setTemplateText = currentSetTemplate.name

    val setTemplateState = TextFieldState(setTemplateText)

    Box(
        modifier = Modifier.then(modifier)
    ) {
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            when (loadState) {
                is DropdownLoadState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

                is DropdownLoadState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = loadState.message)
                    }
                }

                is DropdownLoadState.Idle -> {
                    val setTemplateOptions = loadState.data
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
    }
}