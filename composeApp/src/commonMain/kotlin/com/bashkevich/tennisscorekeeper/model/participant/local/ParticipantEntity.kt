package com.bashkevich.tennisscorekeeper.model.participant.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.participant.remote.DoublesParticipantDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.SinglesParticipantDto
import com.bashkevich.tennisscorekeeper.model.player.local.PlayerEntity
import com.bashkevich.tennisscorekeeper.model.player.local.toEntity

@Entity(
    tableName = "participants",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["second_player_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tournament_id"]),
        Index(value = ["player_id"]),
        Index(value = ["second_player_id"])
    ]
)
data class ParticipantEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "tournament_id")
    val tournamentId: String,
    @ColumnInfo(name = "seed")
    val seed: Int?,
    @ColumnInfo(name = "player_id")
    val playerId: String,
    @ColumnInfo(name = "second_player_id")
    val secondPlayerId: String?,
)

fun ParticipantDto.toEntity(tournamentId: String) = when (this) {
    is SinglesParticipantDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id,
            tournamentId = tournamentId,
            seed = seed,
            playerId = player.id,
            secondPlayerId = null,
        ),
        player = player.toEntity(),
        secondPlayer = null,
    )
    is DoublesParticipantDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id,
            tournamentId = tournamentId,
            seed = seed,
            playerId = firstPlayer.id,
            secondPlayerId = secondPlayer.id,
        ),
        player = firstPlayer.toEntity(),
        secondPlayer = secondPlayer.toEntity(),
    )
}
