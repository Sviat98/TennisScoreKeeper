package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.Embedded
import androidx.room3.Relation
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantEntity
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantWithPlayersEntity
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
