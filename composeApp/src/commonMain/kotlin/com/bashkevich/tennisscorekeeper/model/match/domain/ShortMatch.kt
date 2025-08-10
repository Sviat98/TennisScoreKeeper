package com.bashkevich.tennisscorekeeper.model.match.domain

import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant

data class ShortMatch(
    val id: String,
    val firstParticipant: ParticipantInShortMatch,
    val secondParticipant: ParticipantInShortMatch,
    val status: MatchStatus,
    val previousSets: List<TennisSet>,
    val currentSet: TennisSet? = null,
    val currentGame: TennisGame? = null,
)

fun ShortMatchDto.toDomain() = ShortMatch(
    id = this.id,
    firstParticipant = this.firstParticipant.toDomain(),
    secondParticipant = this.secondParticipant.toDomain(),
    status = this.status,
    previousSets = this.previousSets.map { it.toDomain() },
    currentSet = this.currentSet?.toDomain(),
    currentGame = this.currentGame?.toDomain()
)

val SAMPLE_SINGLES_SHORT_MATCH = ShortMatch(
    id = "1",
    firstParticipant = ParticipantInShortSinglesMatch(
        id = "5",
        seed = 1,
        isWinner = false,
        isRetired = false,
        player = PlayerInParticipant(
            id = "1",
            surname = "Djokovic",
            name = "Novak",
        ),
    ),
    secondParticipant = ParticipantInShortSinglesMatch(
        id = "6",
        seed = null,
        isWinner = false,
        isRetired = false,
        player = PlayerInParticipant(
            id = "4",
            surname = "Murray",
            name = "Andy",
        ),
    ),
    status = MatchStatus.PAUSED,
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 6, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 6),
    ),
//    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
//    currentSetMode = null,
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)

val SAMPLE_DOUBLES_SHORT_MATCH = ShortMatch(
    id = "12",
    firstParticipant = ParticipantInShortDoublesMatch(
        id = "34",
        seed = 1,
        isWinner = true,
        isRetired = false,
        firstPlayer = PlayerInParticipant(
            id = "1",
            surname = "Vennegoor of Hesselink",
            name = "Jan"
        ),
        secondPlayer = PlayerInParticipant(
            id = "3",
            surname = "Vennegoor of Hesselink", name = "Lucas"
        ),
    ),
    secondParticipant = ParticipantInShortDoublesMatch(
        id = "35",
        seed = 2,
        isWinner = false,
        isRetired = false,
        firstPlayer = PlayerInParticipant(
            id = "4",
            surname = "Murray",
            name = "Andy",
        ),
        secondPlayer = PlayerInParticipant(
            id = "6",
            surname = "Federer",
            name = "Roger",
        ),
    ),
    status = MatchStatus.PAUSED,
    previousSets = listOf(
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 3, secondParticipantGamesWon = 10),
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 4),
        TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 6),
    ),
    currentSet = TennisSet(firstParticipantGamesWon = 10, secondParticipantGamesWon = 9),
    currentGame = TennisGame(firstParticipantPointsWon = "30", secondParticipantPointsWon = "15")
)