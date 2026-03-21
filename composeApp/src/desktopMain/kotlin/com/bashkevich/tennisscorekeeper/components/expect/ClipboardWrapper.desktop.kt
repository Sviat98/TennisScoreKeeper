package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual suspend fun Clipboard.setText(text: String) {
    this.setClipEntry(ClipEntry(StringSelection(text)))
}