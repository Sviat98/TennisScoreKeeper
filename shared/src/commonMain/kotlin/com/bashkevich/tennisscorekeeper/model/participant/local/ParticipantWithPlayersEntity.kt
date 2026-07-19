package com.bashkevich.tennisscorekeeper.model.participant.local

import androidx.room3.Embedded
import androidx.room3.Relation
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.SinglesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.player.local.PlayerEntity
import com.bashkevich.tennisscorekeeper.model.player.local.toDomain

data class ParticipantWithPlayersEntity(
    @Embedded val participant: ParticipantEntity,
    @Relation(parentColumns = ["player_id"], entityColumns = ["id"])
    val player: PlayerEntity,
    @Relation(parentColumns = ["second_player_id"], entityColumns = ["id"])
    val secondPlayer: PlayerEntity?,
)

fun ParticipantWithPlayersEntity.toDomain(): TennisParticipant = if (secondPlayer != null) {
    DoublesParticipant(
        id = participant.id,
        seed = participant.seed,
        firstPlayer = player.toDomain(),
        secondPlayer = secondPlayer.toDomain(),
    )
} else {
    SinglesParticipant(
        id = participant.id,
        seed = participant.seed,
        player = player.toDomain(),
    )
}
