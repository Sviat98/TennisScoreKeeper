package com.bashkevich.tennisscorekeeper.model.participant.domain

import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInSinglesMatchDto
import com.bashkevich.tennisscorekeeper.model.player.domain.TennisPlayerInMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.toDomain

sealed class TennisParticipantInMatch{
    abstract val id: String
    abstract val seed: Int?
    abstract val displayName: String
    abstract val isServing: Boolean
    abstract val isWinner: Boolean
}

data class ParticipantInSinglesMatch(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    val player: TennisPlayerInMatch
): TennisParticipantInMatch()

data class ParticipantInDoublesMatch(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    val firstPlayer: TennisPlayerInMatch,
    val secondPlayer: TennisPlayerInMatch
): TennisParticipantInMatch()
fun ParticipantInMatchDto.toDomain() =
    when (this) {
        is ParticipantInSinglesMatchDto -> {
            ParticipantInSinglesMatch(
                id = this.id,
                seed = this.seed,
                displayName = this.displayName,
                isWinner = this.isWinner,
                isServing = this.isServing,
                player = this.player.toDomain()
            )
        }

        is ParticipantInDoublesMatchDto -> {
            ParticipantInDoublesMatch(
                id = this.id,
                seed = this.seed,
                displayName = this.displayName,
                isServing = this.isServing,
                isWinner = this.isWinner,
                firstPlayer = this.firstPlayer.toDomain(),
                secondPlayer = this.secondPlayer.toDomain()
            )
        }
    }

