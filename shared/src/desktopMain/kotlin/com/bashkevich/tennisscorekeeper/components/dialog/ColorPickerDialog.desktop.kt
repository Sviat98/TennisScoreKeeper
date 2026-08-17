package com.bashkevich.tennisscorekeeper.components.dialog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.color_picker

@Composable
actual fun ColorPickerDialog(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit,
    width: Dp,
    height: Dp
) {
    DialogWindow(
        onCloseRequest = onDismissRequest,
        title = stringResource(Res.string.color_picker),
        state = rememberDialogState(size = DpSize(width, height)),
        content = {
            ColorPickerDialogContent(
                modifier = Modifier.fillMaxSize(),
                initialColor = initialColor,
                onColorSelected = onColorSelected
            )
        }
    )
}
