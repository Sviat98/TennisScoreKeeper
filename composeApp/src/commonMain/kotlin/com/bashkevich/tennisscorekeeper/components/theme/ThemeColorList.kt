package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
fun ThemeColorList(
    editedTheme: ScoreboardTheme,
    oldTheme: ScoreboardTheme,
    onColorSelected: (ThemeColorField, Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(max = 360.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ThemeColorField.entries.forEach { field ->
            ThemeColorCard(
                field = field,
                editedTheme = editedTheme,
                oldTheme = oldTheme,
                onColorSelected = onColorSelected,
            )
        }
    }
}
