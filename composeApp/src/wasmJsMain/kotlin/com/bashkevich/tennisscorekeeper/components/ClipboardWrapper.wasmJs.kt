package com.bashkevich.tennisscorekeeper.components

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.setText(text: String) {
    this.setClipEntry(ClipEntry.withPlainText(text))
}