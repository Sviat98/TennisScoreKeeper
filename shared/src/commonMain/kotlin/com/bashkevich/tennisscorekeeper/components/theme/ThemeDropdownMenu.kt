package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Autorenew
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.error_loading_theme
import tennisscorekeeper.shared.generated.resources.loading
import tennisscorekeeper.shared.generated.resources.open_dropdown
import tennisscorekeeper.shared.generated.resources.retry
import tennisscorekeeper.shared.generated.resources.select_theme

@Composable
fun ThemeCombobox(
    modifier: Modifier = Modifier,
    themeComponentState: ThemeComponentState,
    onThemeSelected: (ScoreboardTheme) -> Unit,
    onThemesFetch: () -> Unit = {},
    onRetrySelectedTheme: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val isIdle = themeComponentState.selectedTheme is ThemeComponentState.SelectedThemeState.Idle
    val dropdownEnabled = isIdle

    val text = when (val state = themeComponentState.selectedTheme) {
        is ThemeComponentState.SelectedThemeState.Idle -> state.theme?.name ?: ""
        is ThemeComponentState.SelectedThemeState.Loading -> stringResource(Res.string.loading)
        is ThemeComponentState.SelectedThemeState.Error -> stringResource(Res.string.error_loading_theme)
    }

    val trailingIcon: (@Composable () -> Unit)? = when (val state = themeComponentState.selectedTheme) {
        is ThemeComponentState.SelectedThemeState.Idle -> {
            {
                IconButton(onClick = {
                    expanded = true
                    onThemesFetch()
                }) {
                    Icon(
                        imageVector = IconGroup.Default.ArrowDropDown,
                        contentDescription = stringResource(Res.string.open_dropdown),
                    )
                }
            }
        }
        is ThemeComponentState.SelectedThemeState.Loading -> null
        is ThemeComponentState.SelectedThemeState.Error -> {
            {
                IconButton(onClick = { onRetrySelectedTheme(state.initialThemeId) }) {
                    Icon(
                        imageVector = IconGroup.Default.Autorenew,
                        contentDescription = stringResource(Res.string.retry),
                    )
                }
            }
        }
    }

    val leadingIcon: (@Composable () -> Unit)? = when (val state = themeComponentState.selectedTheme) {
        is ThemeComponentState.SelectedThemeState.Idle -> state.theme?.let { theme -> {
            ThemeColorCircle(
                backgroundColor = theme.mainBackgroundColor,
                textColor = theme.mainTextColor,
            )
        } }
        is ThemeComponentState.SelectedThemeState.Loading -> null
        is ThemeComponentState.SelectedThemeState.Error -> null
    }

    Box(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = TextFieldState(text),
            placeholder = { Text(stringResource(Res.string.select_theme)) },
            readOnly = true,
            enabled = isIdle,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            )
        )

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
                                        ThemeColorCircle(
                                            backgroundColor = theme.mainBackgroundColor,
                                            textColor = theme.mainTextColor,
                                        )
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
fun ThemeColorCircle(
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterStart)
                .background(backgroundColor)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterEnd)
                .background(textColor)
        )
    }
}
