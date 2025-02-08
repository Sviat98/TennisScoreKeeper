package com.bashkevich.tennisscorekeeper.components

import androidx.compose.ui.Modifier

actual fun Modifier.hoverScaleEffect(
    targetScale: Float,
    duration: Int
) : Modifier  = this