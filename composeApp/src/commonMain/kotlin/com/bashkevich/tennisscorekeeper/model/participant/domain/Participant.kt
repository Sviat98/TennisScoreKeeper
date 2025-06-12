package com.bashkevich.tennisscorekeeper.model.participant.domain

import com.bashkevich.tennisscorekeeper.model.participant.remote.DoublesParticipantDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.SinglesParticipantDto
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant
import com.bashkevich.tennisscorekeeper.model.player.domain.toDomain

sealed class TennisParticipant {
    abstract val id: String
    abstract val seed: Int?
}

data class SinglesParticipant(
    override val id: String,
    override val seed: Int?,
    val player: PlayerInParticipant
) : TennisParticipant()

data class DoublesParticipant(
    override val id: String,
    override val seed: Int?,
    val firstPlayer: PlayerInParticipant,
    val secondPlayer: PlayerInParticipant
) : TennisParticipant()

fun ParticipantDto.toDomain(): TennisParticipant = when (this) {
    is SinglesParticipantDto ->
        SinglesParticipant(id = this.id, seed = this.seed, player = this.player.toDomain())

    is DoublesParticipantDto -> DoublesParticipant(
        id = this.id,
        seed = this.seed,
        firstPlayer = this.firstPlayer.toDomain(),
        secondPlayer = this.secondPlayer.toDomain()
    )
}