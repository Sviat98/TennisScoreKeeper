package com.bashkevich.tennisscorekeeper.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PointerButton(modifier: Modifier) {
    var active by remember { mutableStateOf(false) }

    val backgroundColor = if (active) Color.Green else Color.Blue

    val scale by animateFloatAsState(
        targetValue = if (active) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )


    Button(onClick = {}, modifier = Modifier.then(modifier).scale(scale).onPointerEvent(
        PointerEventType.Enter){
        active = true
    }.onPointerEvent(
        PointerEventType.Exit){
        active = false
    }, colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)){
        Text("Sample Button")
    }
}