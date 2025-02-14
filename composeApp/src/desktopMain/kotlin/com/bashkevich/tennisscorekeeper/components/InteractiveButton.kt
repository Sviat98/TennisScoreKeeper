package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun InteractiveButton(
    onClick: () -> Unit,
    onMouseHoveredButtonColor: Color,
    modifier: Modifier,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    elevation: ButtonElevation?,
    shape: Shape,
    border: BorderStroke?,
    colors: ButtonColors,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit
) {
    var active by remember { mutableStateOf(false) }

    val defaultBackgroundColor by colors.backgroundColor(enabled)

    val backgroundColor = if (active) onMouseHoveredButtonColor else defaultBackgroundColor

    Button(
        onClick = onClick,
        modifier = Modifier.then(modifier).onPointerEvent(
            PointerEventType.Enter
        ) {
            active = true
        }.onPointerEvent(
            PointerEventType.Exit
        ) {
            active = false
        },
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        contentPadding = contentPadding,
        content = content
    )
}