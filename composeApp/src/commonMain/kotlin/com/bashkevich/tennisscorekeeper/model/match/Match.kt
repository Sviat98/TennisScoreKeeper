package com.bashkevich.tennisscorekeeper.model.match

data class Match(
    val firstPlayer: TennisPlayer,
    val secondPlayer: TennisPlayer,
    val previousSets: List<TennisSet>,
    val currentSet: TennisSet,
    val currentGame: TennisGame
)

data class TennisPlayer(
    val surname: String,
    val isServing: Boolean
)

data class TennisSet(
    val firstPlayerGamesWon: Int,
    val secondPlayerGamesWon: Int,
)

data class TennisGame(
    val firstPlayerPointsWon: Int,
    val secondPlayerPointsWon: Int,
)

val SAMPLE_MATCH = Match(
    firstPlayer = TennisPlayer(surname = "Djokovic", isServing = false),
    secondPlayer = TennisPlayer(surname = "Auger-Aliassime", isServing = true),
    previousSets = listOf(
        TennisSet(firstPlayerGamesWon = 6, secondPlayerGamesWon = 4),
        TennisSet(firstPlayerGamesWon = 3, secondPlayerGamesWon = 6),
    ),
    currentSet = TennisSet(firstPlayerGamesWon = 10, secondPlayerGamesWon = 9),
    currentGame = TennisGame(firstPlayerPointsWon = 30, secondPlayerPointsWon = 15)
)