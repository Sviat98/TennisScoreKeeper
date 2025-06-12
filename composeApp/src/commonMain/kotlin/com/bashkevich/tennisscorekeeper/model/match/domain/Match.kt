package com.bashkevich.tennisscorekeeper.model.match.domain

import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisGameDto
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisSetDto
import com.bashkevich.tennisscorekeeper.model.match.remote.convertToString
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.player.domain.DoublesPlayerInMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.SinglesPlayerInMatch

data class Match(
    val id: String,
    val pointShift: Int,
    val firstParticipant: TennisParticipantInMatch,
    val secondParticipant: TennisParticipantInMatch,
    val status: String,
    val previousSets: List<TennisSet>,
    val currentSet: TennisSet?,
    val currentSetMode: SpecialSetMode?,
    val currentGame: TennisGame?
)


data class TennisSet(
    val firstParticipantGamesWon: Int,
    val secondParticipantGamesWon: Int,
)

data class TennisGame(
    val firstParticipantPointsWon: String,
    val secondParticipantPointsWon: String,
)

//val EMPTY_TENNIS_GAME = TennisGame("0", "0")
// пока что пустые сеты и геймы равны null
//val EMPTY_TENNIS_SET = TennisSet(0, 0)

fun MatchDto.toDomain() = Match(
    id = this.id,
    pointShift = this.pointShift,
    firstParticipant = this.firstParticipant.toDomain(),
    secondParticipant = this.secondParticipant.toDomain(),
    status = this.status.convertToString(),
    previousSets = this.previousSets.map { it.toDomain() },
    currentSet = this.currentSet?.toDomain(),
    currentSetMode = this.currentSetMode,
    currentGame = this.currentGame?.toDomain()
)


fun TennisSetDto.toDomain() = TennisSet(
    firstParticipantGamesWon = this.firstParticipantGames,
    secondParticipantGamesWon = this.secondParticipantGames,
)

fun TennisGameDto.toDomain() = TennisGame(
    firstParticipantPointsWon = this.firstParticipantPoints,
    secondParticipantPointsWon = this.secondParticipantPoints
)


val SAMPLE_MATCH = Match(
    id = "1",
    pointShift = 0,
    firstParticipant = ParticipantInSinglesMatch(
        id = "1",
        seed = 1,
        displayName = "Djokovic",
        isServing = false,
        isWinner = true,
        player = SinglesPlayerInMatch(id = "1", surname = "Djokovic", name = "Novak")
    ),
    secondParticipant = ParticipantInSinglesMatch(
        id = "2",
        seed = null,
        displayName = "Auger-Aliassime",
        isServing = true,
        isWinner = false,
        player = SinglesPlayerInMatch(id = "2", surname = "Auger-Aliassime", name = "Felix")
    ),
    status = MatchStatus.IN_PROGRESS.convertToString(),
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 6, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 6),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentSetMode = null,
    currentGame = null
        //TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)

val DOUBLES_SAMPLE_MATCH = Match(
    id = "2",
    pointShift = 0,
    firstParticipant = ParticipantInDoublesMatch(
        id = "5",
        seed = 1,
        displayName = "Djokovic/Nadal",
        isServing = false,
        isWinner = false,
        firstPlayer = DoublesPlayerInMatch(
            id = "1",
            surname = "Djokovic",
            name = "Novak",
            isServing = false
        ),
        secondPlayer = DoublesPlayerInMatch(
            id = "3",
            surname = "Nadal",
            name = "Rafael",
            isServing = false
        )
    ),
    secondParticipant = ParticipantInDoublesMatch(
        id = "6",
        seed = null,
        displayName = "Murray/Federer",
        isServing = true,
        isWinner = false,
        firstPlayer = DoublesPlayerInMatch(id = "4", surname = "Murray", name = "Andy", isServing = false),
        secondPlayer = DoublesPlayerInMatch(
            id = "5",
            surname = "Federer",
            name = "Roger",
            isServing = true
        ),
    ),
    status = MatchStatus.IN_PROGRESS.convertToString(),
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 6, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 6),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentSetMode = null,
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)

val SECOND_SAMPLE_MATCH = Match(
    id = "2",
    pointShift = 0,
    firstParticipant = ParticipantInSinglesMatch(
        id = "1",
        seed = 10,
        displayName = "Djokovic",
        isServing = false,
        isWinner = false,
        player = DoublesPlayerInMatch(id = "1", surname = "Djokovic", name = "Novak", isServing = false)
    ),
    secondParticipant = ParticipantInSinglesMatch(
        id = "2",
        seed = null,
        displayName = "Auger-Aliassime",
        isServing = true,
        isWinner = false,
        player = DoublesPlayerInMatch(
            id = "2",
            surname = "Auger-Aliassime",
            name = "Felix",
            isServing = true
        )
    ),
    status = MatchStatus.IN_PROGRESS.convertToString(),
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 12, secondParticipantGamesWon = 10),
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 12),
        TennisSet(firstParticipantGamesWon = 12, secondParticipantGamesWon = 10),
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 12),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentSetMode = null,
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)