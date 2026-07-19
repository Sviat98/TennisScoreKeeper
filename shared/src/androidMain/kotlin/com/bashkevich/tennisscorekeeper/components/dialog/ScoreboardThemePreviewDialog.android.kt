package com.bashkevich.tennisscorekeeper.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
actual fun ScoreboardThemePreviewDialog(
    onDismissRequest: () -> Unit,
    theme: ScoreboardTheme
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            ScoreboardThemePreviewContent(theme = theme)
        }
    )
}
