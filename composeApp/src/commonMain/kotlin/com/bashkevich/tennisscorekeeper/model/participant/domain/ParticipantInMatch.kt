package com.bashkevich.tennisscorekeeper.model.participant.domain

import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInSinglesMatchDto
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.TennisPlayerInMatch

sealed class TennisParticipantInMatch {
    abstract val id: String
    abstract val seed: Int?
    abstract val displayName: String
    abstract val primaryColor: Color
    abstract val secondaryColor: Color?
    abstract val isServing: Boolean
    abstract val isWinner: Boolean
    abstract val isRetired: Boolean
}

data class ParticipantInSinglesMatch(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val primaryColor: Color,
    override val secondaryColor: Color?,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    override val isRetired: Boolean,
    val player: TennisPlayerInMatch
) : TennisParticipantInMatch()

data class ParticipantInDoublesMatch(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val primaryColor: Color,
    override val secondaryColor: Color?,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    override val isRetired: Boolean,
    val firstPlayer: TennisPlayerInMatch,
    val secondPlayer: TennisPlayerInMatch
) : TennisParticipantInMatch()

fun ParticipantInMatchDto.toDomain() =
    when (this) {
        is ParticipantInSinglesMatchDto -> {
            ParticipantInSinglesMatch(
                id = this.id,
                seed = this.seed,
                displayName = this.displayName,
                primaryColor = Color(this.primaryColor.convertColor()),
                secondaryColor = this.secondaryColor?.convertColor()?.let { Color(it) },
                isServing = this.isServing,
                isWinner = this.isWinner,
                isRetired = this.isRetired,
                player = PlayerInSinglesMatch(
                    id = this.player.id,
                    surname = this.player.surname,
                    name = this.player.name,
                )
            )
        }

        is ParticipantInDoublesMatchDto -> {
            fun servingState(playerId: String): Pair<Boolean, Boolean> {
                val isServingNow = this.isServing && this.servingPlayerId == playerId
                val isServingNext = this.servingPlayerId != null &&
                    ((this.isServing && this.servingPlayerId != playerId) ||
                     (!this.isServing && this.servingPlayerId == playerId))
                return Pair(isServingNow, isServingNext)
            }

            val (fpServingNow, fpServingNext) = servingState(this.firstPlayer.id)
            val (spServingNow, spServingNext) = servingState(this.secondPlayer.id)

            ParticipantInDoublesMatch(
                id = this.id,
                seed = this.seed,
                displayName = this.displayName,
                primaryColor = Color(this.primaryColor.convertColor()),
                secondaryColor = this.secondaryColor?.convertColor()?.let { Color(it) },
                isServing = this.isServing,
                isWinner = this.isWinner,
                isRetired = this.isRetired,
                firstPlayer = PlayerInDoublesMatch(
                    id = this.firstPlayer.id,
                    surname = this.firstPlayer.surname,
                    name = this.firstPlayer.name,
                    isServingNow = fpServingNow,
                    isServingNext = fpServingNext,
                ),
                secondPlayer = PlayerInDoublesMatch(
                    id = this.secondPlayer.id,
                    surname = this.secondPlayer.surname,
                    name = this.secondPlayer.name,
                    isServingNow = spServingNow,
                    isServingNext = spServingNext,
                ),
            )
        }
    }

fun String.convertColor() = "FF$this".toLong(16)

val PARTICIPANT_IN_SINGLES_MATCH_DEFAULT = ParticipantInSinglesMatch(
    id = "0",
    seed = null,
    displayName = "",
    primaryColor = Color.White,
    secondaryColor = null,
    isServing = false,
    isWinner = false,
    isRetired = false,
    player = PlayerInSinglesMatch(id = "0", surname = "", name = ""),
)

val PARTICIPANT_IN_DOUBLES_MATCH_DEFAULT = ParticipantInDoublesMatch(
    id = "0",
    seed = null,
    displayName = "",
    primaryColor = Color.White,
    secondaryColor = null,
    isServing = false,
    isWinner = false,
    isRetired = false,
    firstPlayer = PlayerInDoublesMatch(
        id = "0",
        surname = "",
        name = "",
        isServingNow = false,
        isServingNext = false
    ),
    secondPlayer = PlayerInDoublesMatch(
        id = "0",
        surname = "",
        name = "",
        isServingNow = false,
        isServingNext = false
    ),
)
