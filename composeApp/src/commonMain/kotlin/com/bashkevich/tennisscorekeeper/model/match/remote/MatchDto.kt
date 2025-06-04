package com.bashkevich.tennisscorekeeper.model.match.remote

import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInMatchDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MatchDto(
    @SerialName("id")
    val id: String,
    @SerialName("point_shift")
    val pointShift: Int,
    @SerialName("first_participant")
    val firstParticipant: ParticipantInMatchDto,
    @SerialName("second_participant")
    val secondParticipant: ParticipantInMatchDto,
    @SerialName("previous_sets")
    val previousSets: List<TennisSetDto>,
    @SerialName("current_set")
    val currentSet: TennisSetDto,
    @SerialName("current_game")
    val currentGame: TennisGameDto,
)

@Serializable
data class TennisSetDto(
    @SerialName("first_participant_games")
    val firstParticipantGames: Int,
    @SerialName("second_participant_games")
    val secondParticipantGames: Int,
    @SerialName("special_set_mode")
    val specialSetMode: SpecialSetMode? = null,
)

// SUPER_TIEBREAK - помечаем, что это супер-тайбрейк (на клиенте нужно залочить кнопки GAME)
// ENDLESS - там, где gamesToWin > 10, на клиенте должна быть возможность завершить досрочно
enum class SpecialSetMode {
    SUPER_TIEBREAK, ENDLESS
}

@Serializable
data class TennisGameDto(
    @SerialName("first_participant_points")
    val firstParticipantPoints: String,
    @SerialName("second_participant_points")
    val secondParticipantPoints: String,
)