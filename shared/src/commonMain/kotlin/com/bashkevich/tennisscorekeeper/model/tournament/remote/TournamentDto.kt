package com.bashkevich.tennisscorekeeper.model.tournament.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.tournament_status_completed
import tennisscorekeeper.shared.generated.resources.tournament_status_in_progress
import tennisscorekeeper.shared.generated.resources.tournament_status_not_started
import tennisscorekeeper.shared.generated.resources.tournament_type_doubles
import tennisscorekeeper.shared.generated.resources.tournament_type_singles

@Serializable
data class TournamentDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: TournamentType,
    @SerialName("status")
    val status: TournamentStatus,
    @SerialName("regular_set_id")
    val regularSetTemplateId: String? = null,
    @SerialName("deciding_set_id")
    val decidingSetTemplateId: String,
    @SerialName("theme_id")
    val themeId: String,
    @SerialName("sets_to_win")
    val setsToWin: Int,
    @SerialName("total_participants")
    val totalParticipants: Int,
    @SerialName("total_matches")
    val totalMatches: Int,
    @SerialName("uncompleted_matches")
    val uncompletedMatches: Int,
)

enum class TournamentType {
    SINGLES, DOUBLES
}

fun TournamentType.toResource(): StringResource = when (this) {
    TournamentType.SINGLES -> Res.string.tournament_type_singles
    TournamentType.DOUBLES -> Res.string.tournament_type_doubles
}

enum class TournamentStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}

fun TournamentStatus.toResource(): StringResource = when (this) {
    TournamentStatus.NOT_STARTED -> Res.string.tournament_status_not_started
    TournamentStatus.IN_PROGRESS -> Res.string.tournament_status_in_progress
    TournamentStatus.COMPLETED -> Res.string.tournament_status_completed
}