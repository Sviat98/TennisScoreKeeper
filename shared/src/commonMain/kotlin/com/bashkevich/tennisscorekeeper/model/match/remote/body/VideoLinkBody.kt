package com.bashkevich.tennisscorekeeper.model.match.remote.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoLinkBody(
    @SerialName("video_id")
    val videoLink: String
)