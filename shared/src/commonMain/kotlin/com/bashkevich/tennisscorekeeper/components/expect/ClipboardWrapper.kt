package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.setText(text: String)