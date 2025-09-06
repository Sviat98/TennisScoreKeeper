package com.bashkevich.tennisscorekeeper.model.match.remote

import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInMatchDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MatchDto(
    @SerialName("id")
    val id: String,
    @SerialName("point_shift")
    val pointShift: Int,
    @SerialName("video_link")
    val videoLink: String? = null,
    @SerialName("first_participant")
    val firstParticipant: ParticipantInMatchDto,
    @SerialName("second_participant")
    val secondParticipant: ParticipantInMatchDto,
    @SerialName("status")
    val status: MatchStatus,
    @SerialName("previous_sets")
    val previousSets: List<TennisSetDto>,
    @SerialName("current_set")
    val currentSet: TennisSetDto?=null,
    @SerialName("current_set_mode")
    val currentSetMode: SpecialSetMode? = null,
    @SerialName("current_game")
    val currentGame: TennisGameDto?=null,
)

@Serializable
data class TennisSetDto(
    @SerialName("first_participant_games")
    val firstParticipantGames: Int,
    @SerialName("second_participant_games")
    val secondParticipantGames: Int,
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