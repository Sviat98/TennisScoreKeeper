package com.bashkevich.tennisscorekeeper.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.theme_preview

@Composable
actual fun ScoreboardThemePreviewDialog(
    onDismissRequest: () -> Unit,
    theme: ScoreboardTheme
) {
    DialogWindow(
        onCloseRequest = onDismissRequest,
        title = stringResource(Res.string.theme_preview),
        state = rememberDialogState(size = DpSize(width = 280.dp, height = 150.dp)),
        content = {
            ScoreboardThemePreviewContent(theme = theme)
        }
    )
}