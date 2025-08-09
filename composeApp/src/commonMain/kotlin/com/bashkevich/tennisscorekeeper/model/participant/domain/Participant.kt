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

val SINGLES_PARTICIPANT_DEFAULT = SinglesParticipant(
    id = "0",
    seed = null,
    player = PlayerInParticipant(id = "0", surname = "", name = "")
)

data class DoublesParticipant(
    override val id: String,
    override val seed: Int?,
    val firstPlayer: PlayerInParticipant,
    val secondPlayer: PlayerInParticipant
) : TennisParticipant()

data class ParticipantListItem(
    val id: String,
    val seedNumber: String,
    val displayName: String
)

val DOUBLES_PARTICIPANT_DEFAULT = DoublesParticipant(
    id = "0",
    seed = null,
    firstPlayer = PlayerInParticipant(id = "0", surname = "", name = ""),
    secondPlayer = PlayerInParticipant(id = "0", surname = "", name = "")
)


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

fun TennisParticipant.toDisplayFormat(isCompact: Boolean = false) = when (this) {
    is SinglesParticipant -> "${this.player.surname} ${this.player.name}"
    is DoublesParticipant -> {
        if (isCompact){
            "${this.firstPlayer.surname} ${this.firstPlayer.name} /\n${this.secondPlayer.surname} ${this.secondPlayer.name}"
        }else{
            "${this.firstPlayer.surname} ${this.firstPlayer.name} / ${this.secondPlayer.surname} ${this.secondPlayer.name}"
        }
    }
}

fun TennisParticipantInMatch.toDisplayFormat() = when {
    this.id == "0" -> ""
    this is ParticipantInSinglesMatch -> "${this.player.surname} ${this.player.name}"
    this is ParticipantInDoublesMatch -> "${this.firstPlayer.surname} ${this.firstPlayer.name} / ${this.secondPlayer.surname} ${this.secondPlayer.name}"
    else -> ""
}