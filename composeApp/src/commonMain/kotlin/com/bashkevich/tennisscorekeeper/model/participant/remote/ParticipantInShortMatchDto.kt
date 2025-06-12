package com.bashkevich.tennisscorekeeper.model.participant.remote

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInParticipantDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
sealed class ParticipantInShortMatchDto{
    @SerialName("id")
    abstract val id: String
    @SerialName("seed")
    abstract val seed: Int?
    @SerialName("is_winner")
    abstract val isWinner: Boolean
}

@Serializable
@SerialName("singles_participant")
data class ParticipantInShortSinglesMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int? = null,
    @SerialName("is_winner")
    override val isWinner: Boolean,
    @SerialName("player")
    val player: PlayerInParticipantDto
) : ParticipantInShortMatchDto()

@Serializable
@SerialName("doubles_participant")
data class ParticipantInShortDoublesMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int? = null,
    @SerialName("is_winner")
    override val isWinner: Boolean,
    @SerialName("first_player")
    val firstPlayer: PlayerInParticipantDto,
    @SerialName("second_player")
    val secondPlayer: PlayerInParticipantDto,
) : ParticipantInShortMatchDto()