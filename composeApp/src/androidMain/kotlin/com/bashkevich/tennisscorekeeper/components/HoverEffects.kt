package com.bashkevich.tennisscorekeeper.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

actual fun Modifier.hoverScaleEffect(
    targetScale: Float,
    duration: Int
) : Modifier  = this

actual fun Modifier.hoverColorEffect(
    unhoveredColor: Color,
    hoveredColor: Color,
) : Modifier  = this