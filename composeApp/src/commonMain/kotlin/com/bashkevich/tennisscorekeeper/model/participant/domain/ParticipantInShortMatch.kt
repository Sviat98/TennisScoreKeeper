package com.bashkevich.tennisscorekeeper.model.participant.domain

import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortSinglesMatchDto
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant
import com.bashkevich.tennisscorekeeper.model.player.domain.toDomain

sealed class ParticipantInShortMatch {
    abstract val id: String
    abstract val seed: Int?
    abstract val isWinner: Boolean
    abstract val isRetired: Boolean
}

data class ParticipantInShortSinglesMatch(
    override val id: String,
    override val seed: Int?,
    override val isWinner: Boolean,
    override val isRetired: Boolean,
    val player: PlayerInParticipant
) : ParticipantInShortMatch()


data class ParticipantInShortDoublesMatch(
    override val id: String,
    override val seed: Int?,
    override val isWinner: Boolean,
    override val isRetired: Boolean,
    val firstPlayer: PlayerInParticipant,
    val secondPlayer: PlayerInParticipant,
) : ParticipantInShortMatch()

fun ParticipantInShortMatchDto.toDomain(): ParticipantInShortMatch = when (this) {
    is ParticipantInShortSinglesMatchDto -> ParticipantInShortSinglesMatch(
        id = this.id,
        seed = this.seed,
        isWinner = this.isWinner,
        isRetired = this.isRetired,
        player = this.player.toDomain()
    )

    is ParticipantInShortDoublesMatchDto -> ParticipantInShortDoublesMatch(
        id = this.id,
        seed = this.seed,
        isWinner = this.isWinner,
        isRetired = this.isRetired,
        firstPlayer = this.firstPlayer.toDomain(),
        secondPlayer = this.secondPlayer.toDomain()
    )
}

fun PlayerInParticipant.toShortMatchDisplayFormat(): String {
        val firstLetterInName = this.name.substring(0, 1)

        return "$firstLetterInName. ${this.surname}"
    }