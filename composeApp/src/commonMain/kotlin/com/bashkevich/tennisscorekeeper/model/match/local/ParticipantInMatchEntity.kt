package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
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
    val matchId: String,
    @ColumnInfo(name = "participant_id")
    val participantId: String,
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
    val servingPlayerId: String?,
)

fun ParticipantInShortMatchDto.toEntity(matchId: String) = ParticipantInMatchEntity(
    matchId = matchId,
    participantId = id,
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
        firstParticipant.toEntity(matchId = id),
        secondParticipant.toEntity(matchId = id),
    )
}
