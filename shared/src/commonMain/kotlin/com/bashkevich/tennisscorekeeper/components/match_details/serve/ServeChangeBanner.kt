package com.bashkevich.tennisscorekeeper.components.match_details.serve

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.tap_to_change_serve_player

@Composable
fun ServeChangeBanner(
    modifier: Modifier = Modifier,
    visible: Boolean,
    expanded: Boolean,
    onToggleClick: () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Column {
            val arrowRotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                label = "serveChangeArrowRotation"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.tap_to_change_serve_player),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = IconGroup.Default.ArrowDropDown,
                    contentDescription = stringResource(Res.string.tap_to_change_serve_player),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
