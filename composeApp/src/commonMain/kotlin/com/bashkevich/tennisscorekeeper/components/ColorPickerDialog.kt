package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

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
                    .height(35.dp), controller = controller, initialColor = selectedColor
            )
            AlphaTile(
                modifier = Modifier
                    .size(40.dp)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(6.dp))
                    .clip(shape = RoundedCornerShape(6.dp)),
                controller = controller
            )
            Button(onClick = { onColorSelected(selectedColor) }) {
                Text("Select")
            }
        }

    }
}