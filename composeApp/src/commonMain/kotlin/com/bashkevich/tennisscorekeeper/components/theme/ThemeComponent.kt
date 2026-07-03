package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Preview
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.preview

@Composable
fun ThemeComponent(
    modifier: Modifier = Modifier,
    themeComponentState: ThemeComponentState,
    onThemesFetch: () -> Unit,
    onThemeSelected: (ScoreboardTheme) -> Unit,
    onRetrySelectedTheme: (Int) -> Unit,
    onPreviewClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .widthIn(max = 300.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThemeCombobox(
            modifier = Modifier.weight(1f),
            themeComponentState = themeComponentState,
            onThemeSelected = onThemeSelected,
            onThemesFetch = onThemesFetch,
            onRetrySelectedTheme = onRetrySelectedTheme,
        )

        IconButton(onClick = onPreviewClick) {
            Icon(
                imageVector = IconGroup.Default.Preview,
                contentDescription = stringResource(Res.string.preview),
            )
        }
    }
}
