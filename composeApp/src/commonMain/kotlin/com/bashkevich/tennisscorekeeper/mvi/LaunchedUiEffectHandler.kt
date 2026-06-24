package com.bashkevich.tennisscorekeeper.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun <A : UiAction> LaunchedUiEffectHandler(
    effect: A?,
    onConsume: () -> Unit,
    handler: suspend (A) -> Unit,
) {
    LaunchedEffect(effect) {
        val currentEffect = effect ?: return@LaunchedEffect
        handler(currentEffect)
        onConsume()
    }
}
