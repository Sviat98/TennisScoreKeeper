package com.bashkevich.tennisscorekeeper.components.dialog

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun ColorPickerDialog(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit,
    width: Dp,
    height: Dp
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties
            (usePlatformDefaultWidth = false)
    ) {
        ColorPickerDialogContent(
            modifier = Modifier.width(width).height(height),
            initialColor = initialColor,
            onColorSelected = onColorSelected
        )
    }
}
