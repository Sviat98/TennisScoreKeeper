package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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

    val defaultContentColor by colors.contentColor(enabled)


    val interactionSource = remember { MutableInteractionSource() }

    val isHovered by interactionSource.collectIsHoveredAsState()

    val isClicked by interactionSource.collectIsPressedAsState()

    val backgroundColor = when {
        isClicked -> Color.Yellow
        isHovered -> onMouseHoveredButtonColor
        else-> defaultBackgroundColor
    }

    val contentColor = if (isHovered) defaultContentColor else defaultContentColor

    Button(
        onClick = onClick,
        modifier = Modifier.then(modifier).hoverable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = {}),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = contentPadding,
        content = content
    )
}