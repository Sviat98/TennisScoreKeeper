package com.bashkevich.tennisscorekeeper.model.match.domain

import com.bashkevich.tennisscorekeeper.model.match.remote.DoublesParticipantDto
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.ParticipantDto
import com.bashkevich.tennisscorekeeper.model.match.remote.PlayerInMatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.SinglesParticipantDto
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisGameDto
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisSetDto

data class Match(
    val id: String,
    val pointShift: Int,
    val firstParticipant: TennisParticipant,
    val secondParticipant: TennisParticipant,
    val previousSets: List<TennisSet>,
    val currentSet: TennisSet,
    val currentGame: TennisGame
)


data class TennisSet(
    val firstParticipantGamesWon: Int,
    val secondParticipantGamesWon: Int,
    val specialSetMode: SpecialSetMode? = null
)

data class TennisGame(
    val firstParticipantPointsWon: String,
    val secondParticipantPointsWon: String,
)

val EMPTY_TENNIS_GAME = TennisGame("0", "0")

val EMPTY_TENNIS_SET = TennisSet(0, 0)

fun MatchDto.toDomain() = Match(
    id = this.id,
    pointShift = this.pointShift,
    firstParticipant = this.firstParticipant.toDomain(),
    secondParticipant = this.secondParticipant.toDomain(),
    previousSets = this.previousSets.map { it.toDomain() },
    currentSet = this.currentSet.toDomain(),
    currentGame = this.currentGame.toDomain()
)

fun ParticipantDto.toDomain() =
    when (this) {
        is SinglesParticipantDto -> {
            SinglesParticipant(
                id = this.id,
                displayName = this.displayName,
                isServing = this.isServing,
                isWinner = this.isWinner,
                player = this.player.toDomain()
            )
        }

        is DoublesParticipantDto -> {
            DoublesParticipant(
                id = this.id,
                displayName = this.firstPlayer.surname,
                isServing = this.isServing,
                isWinner = this.isWinner,
                firstPlayer = this.firstPlayer.toDomain(),
                secondPlayer = this.secondPlayer.toDomain()
            )
        }
    }

fun TennisSetDto.toDomain() = TennisSet(
    firstParticipantGamesWon = this.firstParticipantGames,
    secondParticipantGamesWon = this.secondParticipantGames,
    specialSetMode = this.specialSetMode
)

fun TennisGameDto.toDomain() = TennisGame(
    firstParticipantPointsWon = this.firstParticipantPoints,
    secondParticipantPointsWon = this.secondParticipantPoints
)


val SAMPLE_MATCH = Match(
    id = "1",
    pointShift = 0,
    firstParticipant = SinglesParticipant(
        id = "1",
        displayName = "Djokovic",
        isServing = false,
        isWinner = false,
        player = SinglesPlayer(id = "1", surname = "Djokovic", name = "Novak")
    ),
    secondParticipant = SinglesParticipant(
        id = "2",
        displayName = "Auger-Aliassime",
        isServing = true,
        isWinner = false,
        player = SinglesPlayer(id = "2", surname = "Auger-Aliassime", name = "Felix")
    ),
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 6, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 6),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)

val SECOND_SAMPLE_MATCH = Match(
    id = "2",
    pointShift = 0,
    firstParticipant = SinglesParticipant(
        id = "1",
        displayName = "Djokovic",
        isServing = false,
        isWinner = false,
        player = SinglesPlayer(id = "1", surname = "Djokovic", name = "Novak")
    ),
    secondParticipant = SinglesParticipant(
        id = "2",
        displayName = "Auger-Aliassime",
        isServing = true,
        isWinner = false,
        player = SinglesPlayer(id = "2", surname = "Auger-Aliassime", name = "Felix")
    ),
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 12, secondParticipantGamesWon = 10),
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 12),
        TennisSet(firstParticipantGamesWon = 12, secondParticipantGamesWon = 10),
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 12),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)