package com.bashkevich.tennisscorekeeper.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

expect fun Modifier.hoverScaleEffect(targetScale: Float = 1.1f, duration: Int = 300): Modifier

expect fun Modifier.hoverColorEffect(
    unhoveredColor: Color = Color.White,
    hoveredColor: Color = Color.LightGray,
): Modifier