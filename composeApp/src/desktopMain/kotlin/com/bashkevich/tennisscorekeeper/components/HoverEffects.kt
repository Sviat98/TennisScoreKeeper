package com.bashkevich.tennisscorekeeper.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.hoverScaleEffect(
    targetScale: Float,
    duration: Int
): Modifier  = composed{
    var isHovered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) targetScale else 1f,
        animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )


    this.scale(scale).onPointerEvent(
        PointerEventType.Enter){
        isHovered = true
    }.onPointerEvent(
        PointerEventType.Exit){
        isHovered = false
    }
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.hoverColorEffect(
    unhoveredColor: Color,
    hoveredColor: Color,
): Modifier  = composed{
    var isHovered by remember { mutableStateOf(false) }

    val backgroundColor = if (isHovered) hoveredColor else unhoveredColor


    this.background(backgroundColor).onPointerEvent(
        PointerEventType.Enter){
        isHovered = true
    }.onPointerEvent(
        PointerEventType.Exit){
        isHovered = false
    }
}