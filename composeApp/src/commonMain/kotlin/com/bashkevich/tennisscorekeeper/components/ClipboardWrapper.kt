package com.bashkevich.tennisscorekeeper.components

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.setText(text: String)