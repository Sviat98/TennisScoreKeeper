package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
fun ThemeComponent(
    modifier: Modifier = Modifier,
    themeComponentState: ThemeComponentState,
    onThemesFetch: () -> Unit,
    onThemeSelected: (ScoreboardTheme) -> Unit,
    onRetrySelectedTheme: () -> Unit = {},
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ThemeCombobox(
                themeComponentState = themeComponentState,
                onThemeSelected = onThemeSelected,
                onThemesFetch = onThemesFetch,
                onRetrySelectedTheme = onRetrySelectedTheme,
            )
        }
    }
}
