package com.bashkevich.tennisscorekeeper.components

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry

actual suspend fun Clipboard.setText(text: String) {
    this.setClipEntry(ClipData.newPlainText("Text copied", text).toClipEntry())
}