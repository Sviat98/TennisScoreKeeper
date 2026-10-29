package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto

@Entity(
    tableName = "matches",
    indices = [
        Index(value = ["tournament_id"]),
        Index(value = ["first_participant_id"]),
        Index(value = ["second_participant_id"])
    ]
)
data class MatchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "tournament_id")
    val tournamentId: Int,
    @ColumnInfo(name = "first_participant_id")
    val firstParticipantId: Int,
    @ColumnInfo(name = "second_participant_id")
    val secondParticipantId: Int,
    @ColumnInfo(name = "point_shift")
    val pointShift: Int,
    @ColumnInfo(name = "video_link")
    val videoLink: String?,
    @ColumnInfo(name = "sets_to_win")
    val setsToWin: Int,
    @ColumnInfo(name = "regular_set_id")
    val regularSetId: Int?,
    @ColumnInfo(name = "deciding_set_id")
    val decidingSetId: Int,
    @ColumnInfo(name = "theme_id")
    val themeId: Int,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "current_set_mode")
    val currentSetMode: String?,
)

// Настройки счёта в ShortMatchDto отсутствуют — пишем плейсхолдеры,
// строку перезапишет полный WS-снапшот при открытии матча (insert REPLACE)
fun ShortMatchDto.toMatchEntity() = MatchEntity(
    id = id.toInt(),
    tournamentId = tournamentId.toInt(),
    firstParticipantId = firstParticipant.id.toInt(),
    secondParticipantId = secondParticipant.id.toInt(),
    pointShift = 0,
    videoLink = null,
    setsToWin = 0,
    regularSetId = null,
    decidingSetId = 0,
    themeId = themeId.toInt(),
    status = status.name,
    currentSetMode = null,
)

fun MatchDto.toMatchEntity() = MatchEntity(
    id = id.toInt(),
    tournamentId = tournamentId.toInt(),
    firstParticipantId = firstParticipant.id.toInt(),
    secondParticipantId = secondParticipant.id.toInt(),
    pointShift = pointShift,
    videoLink = videoLink,
    setsToWin = setsToWin,
    regularSetId = regularSetId?.toInt(),
    decidingSetId = decidingSetId.toInt(),
    themeId = themeId.toInt(),
    status = status.name,
    currentSetMode = currentSetMode?.name,
)
