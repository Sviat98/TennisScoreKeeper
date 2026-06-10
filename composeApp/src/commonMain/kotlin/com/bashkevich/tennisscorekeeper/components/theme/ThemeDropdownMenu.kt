package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
fun ThemeCombobox(
    modifier: Modifier = Modifier,
    themeComponentState: ThemeComponentState,
    onThemeSelected: (ScoreboardTheme) -> Unit,
    onThemesFetch: () -> Unit = {},
    onRetrySelectedTheme: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    val isIdle = themeComponentState.selectedTheme is ThemeComponentState.SelectedThemeState.Idle
    val dropdownEnabled = isIdle

    Box(modifier = modifier) {
        when (val selectedThemeState = themeComponentState.selectedTheme) {
            is ThemeComponentState.SelectedThemeState.Idle -> {
                val textFieldState = TextFieldState(selectedThemeState.theme.name)

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = textFieldState,
                    placeholder = { Text("Select theme") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            expanded = true
                            onThemesFetch()
                        }) {
                            Icon(
                                imageVector = IconGroup.Default.ArrowDropDown,
                                contentDescription = "Open dropdown",
                            )
                        }
                    },
                    leadingIcon = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ColorCircle(color = selectedThemeState.theme.backgroundColor)
                            ColorCircle(color = selectedThemeState.theme.textColor)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.Gray,
                        disabledTrailingIconColor = Color.Gray
                    )
                )
            }

            is ThemeComponentState.SelectedThemeState.Loading -> {
                val textFieldState = TextFieldState("Loading...")

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = textFieldState,
                    readOnly = true,
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.Gray,
                        disabledTrailingIconColor = Color.Gray
                    )
                )
            }

            is ThemeComponentState.SelectedThemeState.Error -> {
                Column {
                    val textFieldState = TextFieldState("Error loading theme")

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = textFieldState,
                        readOnly = true,
                        enabled = false,
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
                        Button(onClick = onRetrySelectedTheme) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        if (dropdownEnabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                when (val optionsState = themeComponentState.themeOptionsState) {
                    is ThemeComponentState.ThemeOptionsState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }

                    is ThemeComponentState.ThemeOptionsState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = optionsState.message)
                        }
                    }

                    is ThemeComponentState.ThemeOptionsState.Idle -> {
                        optionsState.options.forEach { theme ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ColorCircle(color = theme.backgroundColor)
                                        ColorCircle(color = theme.textColor)
                                        Text(
                                            text = theme.name,
                                            color = Color.Black
                                        )
                                    }
                                },
                                onClick = {
                                    onThemeSelected(theme)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorCircle(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
    )
}
