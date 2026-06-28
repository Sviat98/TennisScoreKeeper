package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto

@Entity(
    tableName = "match_games",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["match_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MatchGameEntity(
    @PrimaryKey
    @ColumnInfo(name = "match_id")
    val matchId: String,
    @ColumnInfo(name = "first_participant_points")
    val firstParticipantPoints: String,
    @ColumnInfo(name = "second_participant_points")
    val secondParticipantPoints: String,
)

fun ShortMatchDto.toMatchGameEntity(): MatchGameEntity? {
    val game = currentGame ?: return null
    return MatchGameEntity(
        matchId = id,
        firstParticipantPoints = game.firstParticipantPoints,
        secondParticipantPoints = game.secondParticipantPoints,
    )
}

fun MatchDto.toMatchGameEntity(): MatchGameEntity? {
    val game = currentGame ?: return null
    return MatchGameEntity(
        matchId = id,
        firstParticipantPoints = game.firstParticipantPoints,
        secondParticipantPoints = game.secondParticipantPoints,
    )
}
