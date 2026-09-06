package com.bashkevich.tennisscorekeeper.components.expect

import com.mobilebytelabs.kmptoolkit.share.ExperimentalShareApi
import com.mobilebytelabs.kmptoolkit.share.SharePayload

// On JVM, sharing a Url payload launches the OS "open" handler (i.e. opens the browser),
// there is no system share sheet — Text is the only payload that reaches the clipboard.
@OptIn(ExperimentalShareApi::class)
actual fun shareLinkPayload(url: String): SharePayload = SharePayload.Text(url)
