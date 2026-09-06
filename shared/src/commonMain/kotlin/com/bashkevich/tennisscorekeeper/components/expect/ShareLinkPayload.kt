package com.bashkevich.tennisscorekeeper.components.expect

import com.mobilebytelabs.kmptoolkit.share.ExperimentalShareApi
import com.mobilebytelabs.kmptoolkit.share.SharePayload

@OptIn(ExperimentalShareApi::class)
expect fun shareLinkPayload(url: String): SharePayload
