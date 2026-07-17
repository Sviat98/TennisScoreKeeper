package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Autorenew
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.retry
import tennisscorekeeper.composeapp.generated.resources.theme_load_error

@Composable
fun ThemeLoadErrorRow(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.theme_load_error),
            color = MaterialTheme.colorScheme.error
        )
        IconButton(onClick = onRetry) {
            Icon(
                imageVector = IconGroup.Default.Autorenew,
                contentDescription = stringResource(Res.string.retry)
            )
        }
    }
}
