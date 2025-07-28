package com.bashkevich.tennisscorekeeper.components

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import java.awt.datatransfer.StringSelection

actual suspend fun Clipboard.setText(text: String) {
    this.setClipEntry(ClipEntry(StringSelection(text)))
}