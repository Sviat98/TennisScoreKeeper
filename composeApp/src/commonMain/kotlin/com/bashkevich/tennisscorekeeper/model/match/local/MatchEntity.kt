package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
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
    @ColumnInfo(name = "theme_id")
    val themeId: Int,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "current_set_mode")
    val currentSetMode: String?,
)

fun ShortMatchDto.toMatchEntity(tournamentId: Int) = MatchEntity(
    id = id.toInt(),
    tournamentId = tournamentId,
    firstParticipantId = firstParticipant.id.toInt(),
    secondParticipantId = secondParticipant.id.toInt(),
    pointShift = 0,
    videoLink = null,
    themeId = themeId.toInt(),
    status = status.name,
    currentSetMode = null,
)
