package com.bashkevich.tennisscorekeeper.model.participant.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.participant.remote.DoublesParticipantDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortSinglesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInSinglesMatchDto
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
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["second_player_id"],
            onDelete = ForeignKey.NO_ACTION
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
    val id: Int,
    @ColumnInfo(name = "tournament_id")
    val tournamentId: Int,
    @ColumnInfo(name = "seed")
    val seed: Int?,
    @ColumnInfo(name = "player_id")
    val playerId: Int,
    @ColumnInfo(name = "second_player_id")
    val secondPlayerId: Int?,
)

fun ParticipantDto.toEntity(tournamentId: Int) = when (this) {
    is SinglesParticipantDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id.toInt(),
            tournamentId = tournamentId,
            seed = seed,
            playerId = player.id.toInt(),
            secondPlayerId = null,
        ),
        player = player.toEntity(),
        secondPlayer = null,
    )
    is DoublesParticipantDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id.toInt(),
            tournamentId = tournamentId,
            seed = seed,
            playerId = firstPlayer.id.toInt(),
            secondPlayerId = secondPlayer.id.toInt(),
        ),
        player = firstPlayer.toEntity(),
        secondPlayer = secondPlayer.toEntity(),
    )
}

fun ParticipantInShortMatchDto.toEntity(tournamentId: Int) = when (this) {
    is ParticipantInShortSinglesMatchDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id.toInt(),
            tournamentId = tournamentId,
            seed = seed,
            playerId = player.id.toInt(),
            secondPlayerId = null,
        ),
        player = player.toEntity(),
        secondPlayer = null,
    )
    is ParticipantInShortDoublesMatchDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id.toInt(),
            tournamentId = tournamentId,
            seed = seed,
            playerId = firstPlayer.id.toInt(),
            secondPlayerId = secondPlayer.id.toInt(),
        ),
        player = firstPlayer.toEntity(),
        secondPlayer = secondPlayer.toEntity(),
    )
}

fun ParticipantInMatchDto.toEntity(tournamentId: Int) = when (this) {
    is ParticipantInSinglesMatchDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id.toInt(),
            tournamentId = tournamentId,
            seed = seed,
            playerId = player.id.toInt(),
            secondPlayerId = null,
        ),
        player = player.toEntity(),
        secondPlayer = null,
    )
    is ParticipantInDoublesMatchDto -> ParticipantWithPlayersEntity(
        participant = ParticipantEntity(
            id = id.toInt(),
            tournamentId = tournamentId,
            seed = seed,
            playerId = firstPlayer.id.toInt(),
            secondPlayerId = secondPlayer.id.toInt(),
        ),
        player = firstPlayer.toEntity(),
        secondPlayer = secondPlayer.toEntity(),
    )
}
