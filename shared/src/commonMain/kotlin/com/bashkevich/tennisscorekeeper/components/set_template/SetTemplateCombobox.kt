package com.bashkevich.tennisscorekeeper.components.set_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.error_loading_set_template
import tennisscorekeeper.shared.generated.resources.loading
import tennisscorekeeper.shared.generated.resources.select_set_template

@Composable
fun SetTemplateCombobox(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    setComponentState: SetComponentState,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit,
    onRetrySelectedSet: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val isIdle = setComponentState.selectedSetState is SetComponentState.SelectedSetState.Idle
    val dropdownEnabled = enabled && isIdle

    val text = when (val state = setComponentState.selectedSetState) {
        is SetComponentState.SelectedSetState.Idle -> state.setTemplate?.name ?: ""
        is SetComponentState.SelectedSetState.Loading -> stringResource(Res.string.loading)
        is SetComponentState.SelectedSetState.Error -> stringResource(Res.string.error_loading_set_template)
    }

    // См. комментарий в ThemeDropdownMenu.kt: на Kotlin/Native composable-лямбда создаётся
    // в одной точке под одним условием; выбор иконки — when внутри SetTemplateComboboxTrailingIcon.
    val isSetTemplateLoading =
        setComponentState.selectedSetState is SetComponentState.SelectedSetState.Loading

    Box(
        modifier = Modifier.then(modifier)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = TextFieldState(text),
            placeholder = { Text(
               text =  stringResource(Res.string.select_set_template),
               textAlign = TextAlign.Center
            ) },
            readOnly = true,
            enabled = dropdownEnabled,
            trailingIcon = if (isSetTemplateLoading) {
                null
            } else {
                {
                    SetTemplateComboboxTrailingIcon(
                        state = setComponentState.selectedSetState,
                        dropdownEnabled = dropdownEnabled,
                        onRetrySelectedSet = onRetrySelectedSet,
                        onExpandDropdown = {
                            expanded = true
                            onSetTemplatesFetch()
                        },
                    )
                }
            },
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2, maxHeightInLines = 3),
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

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
