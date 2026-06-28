package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.compose.ui.graphics.Color
import androidx.room3.Embedded
import androidx.room3.Relation
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantEntity
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantWithPlayersEntity
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.player.local.toDomain

data class ParticipantInMatchWithDetails(
    @Embedded val participantInMatch: ParticipantInMatchEntity,
    @Relation(
        entity = ParticipantEntity::class,
        parentColumns = ["participant_id"],
        entityColumns = ["id"]
    )
    val participant: ParticipantWithPlayersEntity,
)

data class MatchWithParticipantsEntity(
    @Embedded val match: MatchEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["match_id"]
    )
    val sets: List<MatchSetEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["match_id"]
    )
    val game: MatchGameEntity?,
    @Relation(
        entity = ParticipantInMatchEntity::class,
        parentColumns = ["first_participant_id"],
        entityColumns = ["participant_id"]
    )
    val firstParticipant: ParticipantInMatchWithDetails,
    @Relation(
        entity = ParticipantInMatchEntity::class,
        parentColumns = ["second_participant_id"],
        entityColumns = ["participant_id"]
    )
    val secondParticipant: ParticipantInMatchWithDetails,
)

fun MatchWithParticipantsEntity.toDomain(): ShortMatch {
    val sortedSets = sets.sortedBy { it.setNumber }
    val previousSets = sortedSets.filter { !it.isCurrent }.map { it.toDomain() }
    val currentSet = sortedSets.find { it.isCurrent }?.toDomain()

    return ShortMatch(
        id = match.id,
        firstParticipant = firstParticipant.toDomain(),
        secondParticipant = secondParticipant.toDomain(),
        status = MatchStatus.valueOf(match.status),
        previousSets = previousSets,
        currentSet = currentSet,
        currentGame = game?.toDomain(),
    )
}

private fun ParticipantInMatchWithDetails.toDomain(): ParticipantInShortMatch {
    return if (participant.secondPlayer != null) {
        ParticipantInShortDoublesMatch(
            id = participantInMatch.participantId,
            seed = participantInMatch.seed,
            isWinner = participantInMatch.isWinner,
            isRetired = participantInMatch.isRetired,
            firstPlayer = participant.player.toDomain(),
            secondPlayer = participant.secondPlayer.toDomain(),
        )
    } else {
        ParticipantInShortSinglesMatch(
            id = participantInMatch.participantId,
            seed = participantInMatch.seed,
            isWinner = participantInMatch.isWinner,
            isRetired = participantInMatch.isRetired,
            player = participant.player.toDomain(),
        )
    }
}

private fun MatchSetEntity.toDomain() = TennisSet(
    firstParticipantGamesWon = firstParticipantGames,
    secondParticipantGamesWon = secondParticipantGames,
)

private fun MatchGameEntity.toDomain() = TennisGame(
    firstParticipantPointsWon = firstParticipantPoints,
    secondParticipantPointsWon = secondParticipantPoints,
)

fun MatchWithParticipantsEntity.toMatchDomain(): Match {
    val sortedSets = sets.sortedBy { it.setNumber }
    val previousSets = sortedSets.filter { !it.isCurrent }.map { it.toDomain() }
    val currentSet = sortedSets.find { it.isCurrent }?.toDomain()

    return Match(
        id = match.id,
        pointShift = match.pointShift,
        videoLink = match.videoLink,
        firstParticipant = firstParticipant.toMatchParticipantDomain(),
        secondParticipant = secondParticipant.toMatchParticipantDomain(),
        status = MatchStatus.valueOf(match.status),
        previousSets = previousSets,
        currentSet = currentSet,
        currentSetMode = match.currentSetMode?.let {
            try { SpecialSetMode.valueOf(it) } catch (_: Exception) { null }
        },
        currentGame = game?.toDomain(),
    )
}

private fun ParticipantInMatchWithDetails.toMatchParticipantDomain(): TennisParticipantInMatch {
    val displayName = participantInMatch.displayName
        .ifEmpty { participant.player.surname + " " + participant.player.name }
    val primaryColor = participantInMatch.primaryColor.toColorOrDefault()
    val secondaryColor = participantInMatch.secondaryColor?.toColorOrDefault()

    return if (participant.secondPlayer != null) {
        val servingState: (String) -> Pair<Boolean, Boolean> = { playerId ->
            val isServingNow = participantInMatch.isServing && participantInMatch.servingPlayerId == playerId
            val isServingNext = participantInMatch.servingPlayerId != null &&
                ((participantInMatch.isServing && participantInMatch.servingPlayerId != playerId) ||
                 (!participantInMatch.isServing && participantInMatch.servingPlayerId == playerId))
            Pair(isServingNow, isServingNext)
        }
        val (fpServingNow, fpServingNext) = servingState(participant.player.id)
        val (spServingNow, spServingNext) = servingState(participant.secondPlayer.id)

        ParticipantInDoublesMatch(
            id = participantInMatch.participantId,
            seed = participantInMatch.seed,
            displayName = displayName,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            isServing = participantInMatch.isServing,
            isWinner = participantInMatch.isWinner,
            isRetired = participantInMatch.isRetired,
            firstPlayer = PlayerInDoublesMatch(
                id = participant.player.id,
                surname = participant.player.surname,
                name = participant.player.name,
                isServingNow = fpServingNow,
                isServingNext = fpServingNext,
            ),
            secondPlayer = PlayerInDoublesMatch(
                id = participant.secondPlayer.id,
                surname = participant.secondPlayer.surname,
                name = participant.secondPlayer.name,
                isServingNow = spServingNow,
                isServingNext = spServingNext,
            ),
        )
    } else {
        ParticipantInSinglesMatch(
            id = participantInMatch.participantId,
            seed = participantInMatch.seed,
            displayName = displayName,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            isServing = participantInMatch.isServing,
            isWinner = participantInMatch.isWinner,
            isRetired = participantInMatch.isRetired,
            player = PlayerInSinglesMatch(
                id = participant.player.id,
                surname = participant.player.surname,
                name = participant.player.name,
            ),
        )
    }
}

private fun String.toColorOrDefault(): Color {
    return if (this.isEmpty()) Color.White
    else try { Color("FF$this".toLong(16)) }
    catch (_: Exception) { Color.White }
}
