package com.bashkevich.tennisscorekeeper.model.participant.remote

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInParticipantDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
sealed class ParticipantDto {
    @SerialName("id")
    abstract val id: String

    @SerialName("seed")
    abstract val seed: Int?
}

@Serializable
@SerialName("singles_participant")
data class SinglesParticipantDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int?,
    @SerialName("player")
    val player: PlayerInParticipantDto,
) : ParticipantDto()

@Serializable
@SerialName("doubles_participant")
data class DoublesParticipantDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int?,
    @SerialName("first_player")
    val firstPlayer: PlayerInParticipantDto,
    @SerialName("second_player")
    val secondPlayer: PlayerInParticipantDto,
) : ParticipantDto()