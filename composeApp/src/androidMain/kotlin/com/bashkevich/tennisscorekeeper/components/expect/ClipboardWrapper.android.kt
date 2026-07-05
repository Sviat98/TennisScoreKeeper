package com.bashkevich.tennisscorekeeper.components.expect

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.text_copied

actual suspend fun Clipboard.setText(text: String) {
    this.setClipEntry(ClipData.newPlainText(getString(Res.string.text_copied), text).toClipEntry())
}