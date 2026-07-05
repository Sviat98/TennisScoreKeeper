package com.bashkevich.tennisscorekeeper.components.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.sets_to_win

@Composable
fun SetsToWinBlock(
    modifier: Modifier = Modifier,
    setsToWin: Int,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.Center
    ) {
            SetsToWinComponent(
                modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
                setsToWin = setsToWin,
                onValueChange = onValueChange
            )
    }
}

@Composable
fun SetsToWinComponent(
    modifier: Modifier = Modifier,
    setsToWin: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(Res.string.sets_to_win))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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