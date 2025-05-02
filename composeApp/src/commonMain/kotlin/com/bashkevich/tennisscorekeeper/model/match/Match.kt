package com.bashkevich.tennisscorekeeper.model.match

import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.PlayerInMatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisGameDto
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisSetDto

data class Match(
    val id: String,
    val pointShift: Int,
    val firstPlayer: TennisPlayer,
    val secondPlayer: TennisPlayer,
    val previousSets: List<TennisSet>,
    val currentSet: TennisSet,
    val currentGame: TennisGame
)

data class TennisPlayer(
    val id: String,
    val surname: String,
    val isServing: Boolean,
    val isWinner: Boolean
)

data class TennisSet(
    val firstPlayerGamesWon: Int,
    val secondPlayerGamesWon: Int,
    val specialSetMode: SpecialSetMode? = null
    )

data class TennisGame(
    val firstPlayerPointsWon: String,
    val secondPlayerPointsWon: String,
)

val EMPTY_TENNIS_GAME = TennisGame("0","0")

val EMPTY_TENNIS_SET = TennisSet(0,0)

fun MatchDto.toDomain() = Match(
    id = this.id,
    pointShift= this.pointShift,
    firstPlayer = this.firstPlayer.toDomain(),
    secondPlayer = this.secondPlayer.toDomain(),
    previousSets = this.previousSets.map { it.toDomain() },
    currentSet = this.currentSet.toDomain(),
    currentGame = this.currentGame.toDomain()
    )

fun PlayerInMatchDto.toDomain() =
    TennisPlayer(id = this.id, surname = this.surname, isServing = this.isServing, isWinner = this.isWinner)

fun TennisSetDto.toDomain() = TennisSet(
    firstPlayerGamesWon = this.firstPlayerGames,
    secondPlayerGamesWon = this.secondPlayerGames,
    specialSetMode = this.specialSetMode
)

fun TennisGameDto.toDomain() = TennisGame(
    firstPlayerPointsWon = this.firstPlayerPoints,
    secondPlayerPointsWon = this.secondPlayerPoints
)


val SAMPLE_MATCH = Match(
    id = "1",
    pointShift = 0,
    firstPlayer = TennisPlayer(id = "1", surname = "Djokovic", isServing = false, isWinner = false),
    secondPlayer = TennisPlayer(id = "2", surname = "Auger-Aliassime", isServing = true, isWinner = false),
    previousSets = listOf(
        TennisSet(firstPlayerGamesWon = 6, secondPlayerGamesWon = 4),
        TennisSet(firstPlayerGamesWon = 3, secondPlayerGamesWon = 6),
    ),
    currentSet = TennisSet(firstPlayerGamesWon = 10, secondPlayerGamesWon = 9),
    currentGame = TennisGame(firstPlayerPointsWon = "30", secondPlayerPointsWon = "15")
)

val SECOND_SAMPLE_MATCH = Match(
    id = "2",
    pointShift = 0,
    firstPlayer = TennisPlayer(id = "1", surname = "Djokovic", isServing = false, isWinner = false),
    secondPlayer = TennisPlayer(id = "2", surname = "Auger-Aliassime", isServing = true, isWinner = false),
    previousSets = listOf(
        TennisSet(firstPlayerGamesWon = 12, secondPlayerGamesWon = 10),
        TennisSet(firstPlayerGamesWon = 10, secondPlayerGamesWon = 12),
        TennisSet(firstPlayerGamesWon = 12, secondPlayerGamesWon = 10),
        TennisSet(firstPlayerGamesWon = 10, secondPlayerGamesWon = 12),
    ),
    currentSet = TennisSet(firstPlayerGamesWon = 10, secondPlayerGamesWon = 9),
    currentGame = TennisGame(firstPlayerPointsWon = "30", secondPlayerPointsWon = "15")
)