package com.bashkevich.tennisscorekeeper.components.match_details.serve

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.tap_to_change_serve_player
import kotlin.math.roundToInt

@Composable
fun ServeChangeBanner(
    modifier: Modifier = Modifier,
    visible: Boolean,
    expanded: Boolean,
    onToggleClick: () -> Unit
) {
    val revealFraction by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "serveChangeBannerReveal"
    )

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "serveChangeArrowRotation"
    )

    // слот всегда занимает высоту контента баннера - кнопки счета под ним не прыгают,
    // а сам баннер выезжает сверху вниз внутри слота (смещение + прозрачность)
    Layout(
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = revealFraction
                    }
                    .clickable { onToggleClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
        },
        modifier = modifier.clipToBounds()
    ) { measurables, constraints ->
        val banner = measurables.first().measure(
            Constraints(maxWidth = constraints.maxWidth)
        )

        layout(constraints.maxWidth, banner.height) {
            banner.placeRelative(
                x = 0,
                y = (-(1f - revealFraction) * banner.height).roundToInt()
            )
        }
    }
}
