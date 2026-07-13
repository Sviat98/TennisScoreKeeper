package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.select
import tennisscorekeeper.composeapp.generated.resources.selected_color

private val DEFAULT_COLORS = listOf(
    Color(0xFFFF0000), // Red
    Color(0xFF800000), // Maroon
    Color(0xFFFFFF00), // Yellow
    Color(0xFF808000), // Olive
    Color(0xFF008000), // Green
    Color(0xFF00FF00), // Lime
    Color(0xFF00FFFF), // Aqua
    Color(0xFF008080), // Teal
    Color(0xFF0000FF), // Blue
    Color(0xFF000080), // Navy
    Color(0xFFFF00FF), // Fuchsia
    Color(0xFF800080), // Purple
    Color(0xFF000000), // Black
    Color(0xFFFFFFFF), // White
    Color(0xFF808080), // Gray
)

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    val controller = rememberColorPickerController()

    controller.debounceDuration = 500L
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HsvColorPicker(
                modifier = Modifier.size(128.dp),
                initialColor = selectedColor,
                controller = controller,
                onColorChanged = {
                    selectedColor = it.color
                })

            BrightnessSlider(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp),
                controller = controller,
                initialColor = selectedColor
            )

            DefaultColorGrid(
                onColorSelected = { color ->
                    selectedColor = color
                    controller.selectByColor(color, fromUser = false)
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(Res.string.selected_color),
                    style = MaterialTheme.typography.bodyMedium,
                )
                ColorBox(color = selectedColor)
            }

            Button(onClick = { onColorSelected(selectedColor) }) {
                Text(stringResource(Res.string.select))
            }
        }
    }
}

@Composable
private fun DefaultColorGrid(
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DEFAULT_COLORS.chunked(5).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                rowColors.forEach { color ->
                    ColorBox(
                        color = color,
                        modifier = Modifier.clickable { onColorSelected(color) },
                        size = 28.dp
                    )
                }
            }
        }
    }
}
