package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.ColorBox
import com.bashkevich.tennisscorekeeper.components.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Undo
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.old_value
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_game_background
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_game_text
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_set_background
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_set_text
import tennisscorekeeper.composeapp.generated.resources.theme_color_main_background
import tennisscorekeeper.composeapp.generated.resources.theme_color_main_text
import tennisscorekeeper.composeapp.generated.resources.theme_color_previous_set_lose
import tennisscorekeeper.composeapp.generated.resources.theme_color_previous_set_win
import tennisscorekeeper.composeapp.generated.resources.theme_color_serve
import tennisscorekeeper.composeapp.generated.resources.undo

@Composable
fun ThemeColorCard(
    field: ThemeColorField,
    editedTheme: ScoreboardTheme,
    oldTheme: ScoreboardTheme,
    onColorSelected: (ThemeColorField, Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val currentColor = field.getColor(editedTheme)
    val oldColor = field.getColor(oldTheme)
    val hasChanged = currentColor != oldColor

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = field.displayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                ColorBox(
                    color = currentColor,
                    modifier = Modifier.clickable { showColorPicker = true }
                )
            }
            AnimatedVisibility(
                visible = hasChanged,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.old_value),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    ColorBox(color = oldColor)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { onColorSelected(field, oldColor) }) {
                        Icon(
                            imageVector = IconGroup.Default.Undo,
                            contentDescription = stringResource(Res.string.undo)
                        )
                    }
                }
            }
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = currentColor,
            onDismissRequest = { showColorPicker = false },
            onColorSelected = {
                showColorPicker = false
                onColorSelected(field, it)
            }
        )
    }
}

@Composable
private fun ThemeColorField.displayName(): String = when (this) {
    ThemeColorField.MAIN_BACKGROUND_COLOR -> stringResource(Res.string.theme_color_main_background)
    ThemeColorField.MAIN_TEXT_COLOR -> stringResource(Res.string.theme_color_main_text)
    ThemeColorField.SERVE_COLOR -> stringResource(Res.string.theme_color_serve)
    ThemeColorField.PREVIOUS_SET_WIN_TEXT_COLOR -> stringResource(Res.string.theme_color_previous_set_win)
    ThemeColorField.PREVIOUS_SET_LOSE_TEXT_COLOR -> stringResource(Res.string.theme_color_previous_set_lose)
    ThemeColorField.CURRENT_SET_BACKGROUND_COLOR -> stringResource(Res.string.theme_color_current_set_background)
    ThemeColorField.CURRENT_SET_TEXT_COLOR -> stringResource(Res.string.theme_color_current_set_text)
    ThemeColorField.CURRENT_GAME_BACKGROUND_COLOR -> stringResource(Res.string.theme_color_current_game_background)
    ThemeColorField.CURRENT_GAME_TEXT_COLOR -> stringResource(Res.string.theme_color_current_game_text)
}
