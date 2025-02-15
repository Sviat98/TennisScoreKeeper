package com.bashkevich.tennisscorekeeper.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

fun Modifier.hoverScaleEffect(targetScale: Float = 1.1f, duration: Int = 300): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }

    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (isHovered) targetScale else 1f,
        animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )

    this.hoverable(interactionSource = interactionSource).scale(scale)
}
