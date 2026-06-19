package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantEntity
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentEntity

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = TournamentEntity::class,
            parentColumns = ["id"],
            childColumns = ["tournament_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ParticipantEntity::class,
            parentColumns = ["id"],
            childColumns = ["first_participant_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ParticipantEntity::class,
            parentColumns = ["id"],
            childColumns = ["second_participant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tournament_id"]),
        Index(value = ["first_participant_id"]),
        Index(value = ["second_participant_id"])
    ]
)
data class MatchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "tournament_id")
    val tournamentId: String,
    @ColumnInfo(name = "first_participant_id")
    val firstParticipantId: String,
    @ColumnInfo(name = "second_participant_id")
    val secondParticipantId: String,
    @ColumnInfo(name = "point_shift")
    val pointShift: Int,
    @ColumnInfo(name = "video_link")
    val videoLink: String?,
    @ColumnInfo(name = "theme_id")
    val themeId: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "current_set_mode")
    val currentSetMode: String?,
)

fun ShortMatchDto.toMatchEntity(tournamentId: String) = MatchEntity(
    id = id,
    tournamentId = tournamentId,
    firstParticipantId = firstParticipant.id,
    secondParticipantId = secondParticipant.id,
    pointShift = 0,
    videoLink = null,
    themeId = themeId,
    status = status.name,
    currentSetMode = null,
)
