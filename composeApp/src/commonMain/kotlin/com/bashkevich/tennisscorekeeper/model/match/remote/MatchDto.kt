package com.bashkevich.tennisscorekeeper.model.match.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MatchDto(
    @SerialName("id")
    val id: String,
    @SerialName("point_shift")
    val pointShift: Int,
    @SerialName("first_player")
    val firstPlayer: PlayerInMatchDto,
    @SerialName("second_player")
    val secondPlayer: PlayerInMatchDto,
    @SerialName("previous_sets")
    val previousSets: List<TennisSetDto>,
    @SerialName("current_set")
    val currentSet: TennisSetDto,
    @SerialName("current_game")
    val currentGame: TennisGameDto,
)

@Serializable
data class PlayerInMatchDto(
    @SerialName("id")
    val id: String,
    @SerialName("surname")
    val surname: String,
    @SerialName("name")
    val name: String,
    @SerialName("is_serving")
    val isServing: Boolean,
    @SerialName("is_winner")
    val isWinner: Boolean
)

@Serializable
data class TennisSetDto(
    @SerialName("first_player_games")
    val firstPlayerGames: Int,
    @SerialName("second_player_games")
    val secondPlayerGames: Int,
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
    @SerialName("first_player_points")
    val firstPlayerPoints: String,
    @SerialName("second_player_points")
    val secondPlayerPoints: String,
)