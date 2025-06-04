package com.bashkevich.tennisscorekeeper.model.participant.remote

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInMatchDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ParticipantInMatchDto {
    @SerialName("id")
    abstract val id: String

    @SerialName("seed")
    abstract val seed: Int?

    @SerialName("display_name")
    abstract val displayName: String

    @SerialName("is_serving")
    abstract val isServing: Boolean

    @SerialName("is_winner")
    abstract val isWinner: Boolean
}

@Serializable
@SerialName("singles_participant")
data class SinglesParticipantInMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int? = null,
    @SerialName("display_name")
    override val displayName: String,
    @SerialName("is_serving")
    override val isServing: Boolean,
    @SerialName("is_winner")
    override val isWinner: Boolean,
    @SerialName("player")
    val player: PlayerInMatchDto,
) : ParticipantInMatchDto()


@Serializable
@SerialName("doubles_participant")
data class DoublesParticipantInMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int? = null,
    @SerialName("display_name")
    override val displayName: String,
    @SerialName("is_serving")
    override val isServing: Boolean,
    @SerialName("is_winner")
    override val isWinner: Boolean,
    @SerialName("first_player")
    val firstPlayer: PlayerInMatchDto,
    @SerialName("second_player")
    val secondPlayer: PlayerInMatchDto,
) : ParticipantInMatchDto()