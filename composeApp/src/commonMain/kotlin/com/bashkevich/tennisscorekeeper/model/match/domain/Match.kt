package com.bashkevich.tennisscorekeeper.model.match.domain

import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisGameDto
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisSetDto
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInSinglesMatch

data class Match(
    val id: Int,
    val pointShift: Int,
    val videoLink: String?,
    val firstParticipant: TennisParticipantInMatch,
    val secondParticipant: TennisParticipantInMatch,
    val status: MatchStatus,
    val previousSets: List<TennisSet>,
    val currentSet: TennisSet?,
    val currentSetMode: SpecialSetMode?,
    val currentGame: TennisGame?,
    val themeId: Int
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
val EMPTY_TENNIS_SET = TennisSet(firstParticipantGamesWon = 0, secondParticipantGamesWon = 0)

fun MatchDto.toDomain() = Match(
    id = this.id.toInt(),
    pointShift = this.pointShift,
    videoLink = this.videoLink,
    firstParticipant = this.firstParticipant.toDomain(),
    secondParticipant = this.secondParticipant.toDomain(),
    status = this.status,
    previousSets = this.previousSets.map { it.toDomain() },
    currentSet = this.currentSet?.toDomain(),
    currentSetMode = this.currentSetMode,
    currentGame = this.currentGame?.toDomain(),
    themeId = this.themeId.toInt()
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
    id = -1,
    pointShift = 0,
    videoLink = null,
    firstParticipant = ParticipantInSinglesMatch(
        id = 1,
        seed = 1,
        //displayName = "DJOKOVIC",
        displayName = "VENNEGOOR OF HESSELINK",
        primaryColor = Color.Green,
        secondaryColor = Color.Blue,
        isServing = false,
        isWinner = false,
        isRetired = false,
        player = PlayerInSinglesMatch(id = 1, surname = "Djokovic", name = "Novak")
    ),
    secondParticipant = ParticipantInSinglesMatch(
        id = 2,
        seed = null,
        displayName = "AUGER-ALIASSIME",
        primaryColor = Color.Red,
        secondaryColor = null,
        isServing = true,
        isWinner = false,
        isRetired = false,
        player = PlayerInSinglesMatch(id = 2, surname = "Auger-Aliassime", name = "Felix")
    ),
    status = MatchStatus.IN_PROGRESS,
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 6, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 6),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentSetMode = null,
    currentGame = null,
    themeId = 0
    //TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)

val DOUBLES_SAMPLE_MATCH = Match(
    id = -2,
    pointShift = 0,
    videoLink = null,
    firstParticipant = ParticipantInDoublesMatch(
        id = 0,
        seed = 1,
        displayName = "ИВАНОВ/ПЕТРОВ",
        primaryColor = Color.White,
        secondaryColor = null,
        isServing = false,
        isWinner = false,
        isRetired = false,
        firstPlayer = PlayerInDoublesMatch(
            id = 0,
            surname = "Иванов",
            name = "Иван",
            isServingNow = false,
            isServingNext = true
        ),
        secondPlayer = PlayerInDoublesMatch(
            id = 0,
            surname = "Петров",
            name = "Петр",
            isServingNow = false,
            isServingNext = false
        )
    ),
    secondParticipant = ParticipantInDoublesMatch(
        id = 0,
        seed = null,
        displayName = "КУЗНЕЦОВ/СИДОРОВ",
        primaryColor = Color.White,
        secondaryColor = null,
        isServing = true,
        isWinner = false,
        isRetired = false,
        firstPlayer = PlayerInDoublesMatch(
            id = 0,
            surname = "Кузнецов",
            name = "Егор",
            isServingNow = false,
            isServingNext = false
        ),
        secondPlayer = PlayerInDoublesMatch(
            id = 0,
            surname = "Сидоров",
            name = "Андрей",
            isServingNow = true,
            isServingNext = false
        ),
    ),
    status = MatchStatus.IN_PROGRESS,
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 6, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 6),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentSetMode = null,
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15"),
    themeId = 0
)
