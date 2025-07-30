package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun SandboxPreviewComponent() {
    SandboxComponent()
}

@Composable
@Preview
fun SampleCanvas(
    diameter: Dp = 128.dp
){
    Canvas(
        modifier = Modifier.requiredSize(diameter)
    ){
        val canvasSize = this.size.minDimension
        val center = Offset(canvasSize / 2, canvasSize / 2)
        val radius = canvasSize / 2
        val path = Path().apply {
            arcTo(
                rect = Rect(
                    center.x - radius / 2,
                    0f,
                    center.x + radius / 2,
                    center.y
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
        }
        drawPath(path, Color.White)
    }
}
