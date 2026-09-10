package com.bashkevich.tennisscorekeeper.components.expect

import com.mobilebytelabs.kmptoolkit.share.ExperimentalShareApi
import com.mobilebytelabs.kmptoolkit.share.SharePayload
import org.jetbrains.compose.resources.StringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.copy_link_to_panel
import tennisscorekeeper.shared.generated.resources.copy_link_to_scoreboard

// On JVM, sharing a Url payload launches the OS "open" handler (i.e. opens the browser),
// there is no system share sheet — Text is the only payload that reaches the clipboard.
@OptIn(ExperimentalShareApi::class)
actual fun shareLinkPayload(url: String): SharePayload = SharePayload.Text(url)

actual fun shareScoreboardMenuItemLabel(): StringResource = Res.string.copy_link_to_scoreboard

actual fun sharePanelMenuItemLabel(): StringResource = Res.string.copy_link_to_panel
