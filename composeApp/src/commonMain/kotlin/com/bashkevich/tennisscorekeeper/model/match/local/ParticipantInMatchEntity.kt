package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortMatchDto

@Entity(
    tableName = "participants_in_match",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["match_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["match_id", "participant_id"],
    indices = [
        Index(value = ["participant_id"])
    ]
)
data class ParticipantInMatchEntity(
    @ColumnInfo(name = "match_id")
    val matchId: Int,
    @ColumnInfo(name = "participant_id")
    val participantId: Int,
    @ColumnInfo(name = "seed")
    val seed: Int?,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "primary_color")
    val primaryColor: String,
    @ColumnInfo(name = "secondary_color")
    val secondaryColor: String?,
    @ColumnInfo(name = "is_serving")
    val isServing: Boolean,
    @ColumnInfo(name = "is_winner")
    val isWinner: Boolean,
    @ColumnInfo(name = "is_retired")
    val isRetired: Boolean,
    @ColumnInfo(name = "serving_player_id")
    val servingPlayerId: Int?,
)

fun ParticipantInShortMatchDto.toEntity(matchId: Int) = ParticipantInMatchEntity(
    matchId = matchId,
    participantId = id.toInt(),
    seed = seed,
    displayName = "",
    primaryColor = "",
    secondaryColor = null,
    isServing = false,
    isWinner = isWinner,
    isRetired = isRetired,
    servingPlayerId = null,
)

fun ShortMatchDto.toParticipantInMatchEntities(): List<ParticipantInMatchEntity> {
    return listOf(
        firstParticipant.toEntity(matchId = id.toInt()),
        secondParticipant.toEntity(matchId = id.toInt()),
    )
}

fun ParticipantInMatchDto.toEntity(matchId: Int): ParticipantInMatchEntity = ParticipantInMatchEntity(
    matchId = matchId,
    participantId = id.toInt(),
    seed = seed,
    displayName = displayName,
    primaryColor = primaryColor,
    secondaryColor = secondaryColor,
    isServing = isServing,
    isWinner = isWinner,
    isRetired = isRetired,
    servingPlayerId = when (this) {
        is ParticipantInDoublesMatchDto -> servingPlayerId?.toInt()
        else -> null
    },
)

fun MatchDto.toParticipantInMatchEntities(): List<ParticipantInMatchEntity> {
    return listOf(
        firstParticipant.toEntity(matchId = id.toInt()),
        secondParticipant.toEntity(matchId = id.toInt()),
    )
}
