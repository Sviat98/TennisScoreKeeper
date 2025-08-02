package com.bashkevich.tennisscorekeeper.components.match.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@Composable
fun SetsToWinComponent(
    modifier: Modifier = Modifier,
    setsToWin: Int,
    onValueChange: (Int) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val alignment = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) Alignment.CenterHorizontally else Alignment.Start

    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment)
    ) {
        Text("Sets to win")
        ToggleSetsToWinButton(
            modifier = Modifier.size(32.dp),
            onClick = { onValueChange(-1) },
            text = "-",
            enabled = setsToWin > 1
        )
        Text(setsToWin.toString())
        ToggleSetsToWinButton(
            modifier = Modifier.size(32.dp),
            onClick = { onValueChange(1) },
            text = "+",
            enabled = setsToWin < 3
        )
    }
}


@Composable
fun ToggleSetsToWinButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean,
) {
    TextButton(
        modifier = Modifier.then(modifier),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text)
    }
}