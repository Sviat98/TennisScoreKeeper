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

    @SerialName("primary_color")
    abstract val primaryColor: String

    @SerialName("secondary_color")
    abstract val secondaryColor: String?

    @SerialName("is_serving")
    abstract val isServing: Boolean

    @SerialName("is_winner")
    abstract val isWinner: Boolean

    @SerialName("is_retired")
    abstract val isRetired: Boolean
}

@Serializable
@SerialName("singles_participant")
data class ParticipantInSinglesMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int? = null,
    @SerialName("display_name")
    override val displayName: String,
    @SerialName("primary_color")
    override val primaryColor: String,
    @SerialName("secondary_color")
    override val secondaryColor: String? = null,
    @SerialName("is_serving")
    override val isServing: Boolean,
    @SerialName("is_winner")
    override val isWinner: Boolean,
    @SerialName("is_retired")
    override val isRetired: Boolean,
    @SerialName("player")
    val player: PlayerInMatchDto,
) : ParticipantInMatchDto()


@Serializable
@SerialName("doubles_participant")
data class ParticipantInDoublesMatchDto(
    @SerialName("id")
    override val id: String,
    @SerialName("seed")
    override val seed: Int? = null,
    @SerialName("display_name")
    override val displayName: String,
    @SerialName("primary_color")
    override val primaryColor: String,
    @SerialName("secondary_color")
    override val secondaryColor: String? = null,
    @SerialName("is_serving")
    override val isServing: Boolean,
    @SerialName("is_winner")
    override val isWinner: Boolean,
    @SerialName("is_retired")
    override val isRetired: Boolean,
    @SerialName("first_player")
    val firstPlayer: PlayerInMatchDto,
    @SerialName("second_player")
    val secondPlayer: PlayerInMatchDto,
) : ParticipantInMatchDto()