package com.bashkevich.tennisscorekeeper.components.set_template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
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
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Composable
fun SetTemplateCombobox(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    setComponentState: SetComponentState,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit,
    onRetrySelectedSet: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    val isIdle = setComponentState.selectedSetState is SetComponentState.SelectedSetState.Idle
    val dropdownEnabled = enabled && isIdle

    Box(
        modifier = Modifier.then(modifier)
    ) {
        when (val selectedState = setComponentState.selectedSetState) {
            is SetComponentState.SelectedSetState.Idle -> {
                val textFieldState = TextFieldState(selectedState.setTemplate?.name ?: "")

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = textFieldState,
                    placeholder = { Text("Set Template") },
                    readOnly = true,
                    enabled = enabled,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                expanded = true
                                onSetTemplatesFetch()
                            },
                            enabled = dropdownEnabled
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
            }

            is SetComponentState.SelectedSetState.Loading -> {
                val textFieldState = TextFieldState("Loading...")

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = textFieldState,
                    readOnly = true,
                    enabled = false,
                    lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2, maxHeightInLines = 3),
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.Gray,
                        disabledTrailingIconColor = Color.Gray
                    )
                )
            }

            is SetComponentState.SelectedSetState.Error -> {
                Column {
                    val textFieldState = TextFieldState("Error loading set template")

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = textFieldState,
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            IconButton(
                                onClick = {},
                                enabled = false
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Button(onClick = onRetrySelectedSet) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        if (dropdownEnabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                when (val optionsState = setComponentState.setOptionsState) {
                    is SetComponentState.SetTemplateOptionsState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }

                    is SetComponentState.SetTemplateOptionsState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = optionsState.message)
                        }
                    }

                    is SetComponentState.SetTemplateOptionsState.Idle -> {
                        optionsState.options.forEach { option ->
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
}
