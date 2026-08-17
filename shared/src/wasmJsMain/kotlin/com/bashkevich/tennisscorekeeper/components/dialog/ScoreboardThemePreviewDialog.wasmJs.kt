package com.bashkevich.tennisscorekeeper.components.dialog

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
actual fun ScoreboardThemePreviewDialog(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    theme: ScoreboardTheme,
    width: Dp,
    height: Dp
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            ScoreboardThemePreviewContent(
                modifier = Modifier.width(width).height(height),
                theme = theme
            )
        }
    )
}
