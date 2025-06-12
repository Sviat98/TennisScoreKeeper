package com.bashkevich.tennisscorekeeper.model.match.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchStatusBody(
    @SerialName("status")
    val status: MatchStatus
)

enum class MatchStatus{
    NOT_STARTED,IN_PROGRESS,PAUSED,COMPLETED
}

fun MatchStatus.convertToString() = this.name.lowercase().replace('_', ' ')