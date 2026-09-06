package com.bashkevich.tennisscorekeeper.components.expect

import com.mobilebytelabs.kmptoolkit.share.ExperimentalShareApi
import com.mobilebytelabs.kmptoolkit.share.SharePayload
import org.jetbrains.compose.resources.StringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.share_link_to_panel
import tennisscorekeeper.shared.generated.resources.share_link_to_scoreboard

@OptIn(ExperimentalShareApi::class)
actual fun shareLinkPayload(url: String): SharePayload = SharePayload.Url(url)

actual fun shareScoreboardMenuItemLabel(): StringResource = Res.string.share_link_to_scoreboard

actual fun sharePanelMenuItemLabel(): StringResource = Res.string.share_link_to_panel
