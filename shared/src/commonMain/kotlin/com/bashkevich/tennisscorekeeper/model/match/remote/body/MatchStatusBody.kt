package com.bashkevich.tennisscorekeeper.model.match.remote.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.match_status_completed
import tennisscorekeeper.shared.generated.resources.match_status_in_progress
import tennisscorekeeper.shared.generated.resources.match_status_not_started
import tennisscorekeeper.shared.generated.resources.match_status_paused

@Serializable
data class MatchStatusBody(
    @SerialName("status")
    val status: MatchStatus
)

enum class MatchStatus{
    NOT_STARTED,IN_PROGRESS,PAUSED,COMPLETED
}

fun MatchStatus.toResource(): StringResource = when (this) {
    MatchStatus.NOT_STARTED -> Res.string.match_status_not_started
    MatchStatus.IN_PROGRESS -> Res.string.match_status_in_progress
    MatchStatus.PAUSED -> Res.string.match_status_paused
    MatchStatus.COMPLETED -> Res.string.match_status_completed
}