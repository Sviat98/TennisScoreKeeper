package com.bashkevich.tennisscorekeeper.components.expect

import com.mobilebytelabs.kmptoolkit.share.ExperimentalShareApi
import com.mobilebytelabs.kmptoolkit.share.SharePayload
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalShareApi::class)
expect fun shareLinkPayload(url: String): SharePayload

expect fun shareScoreboardMenuItemLabel(): StringResource

expect fun sharePanelMenuItemLabel(): StringResource
