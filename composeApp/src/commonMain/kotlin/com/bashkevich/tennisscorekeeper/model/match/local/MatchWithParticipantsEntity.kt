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
        parentColumns = ["id"],
        entityColumns = ["match_id"]
    )
    val participantsInMatch: List<ParticipantInMatchEntity>,
    @Relation(
        entity = ParticipantEntity::class,
        parentColumns = ["first_participant_id"],
        entityColumns = ["id"]
    )
    val firstParticipant: ParticipantWithPlayersEntity,
    @Relation(
        entity = ParticipantEntity::class,
        parentColumns = ["second_participant_id"],
        entityColumns = ["id"]
    )
    val secondParticipant: ParticipantWithPlayersEntity,
)

fun MatchWithParticipantsEntity.toDomain(): ShortMatch {
    val firstPim = participantsInMatch.find { it.participantId == match.firstParticipantId }!!
    val secondPim = participantsInMatch.find { it.participantId == match.secondParticipantId }!!

    val firstParticipantDomain = firstParticipant.toShortMatchDomain(firstPim)
    val secondParticipantDomain = secondParticipant.toShortMatchDomain(secondPim)

    val sortedSets = sets.sortedBy { it.setNumber }
    val previousSets = sortedSets.filter { !it.isCurrent }.map { it.toDomain() }
    val currentSet = sortedSets.find { it.isCurrent }?.toDomain()

    return ShortMatch(
        id = match.id,
        firstParticipant = firstParticipantDomain,
        secondParticipant = secondParticipantDomain,
        status = MatchStatus.valueOf(match.status),
        previousSets = previousSets,
        currentSet = currentSet,
        currentGame = game?.toDomain(),
    )
}

private fun ParticipantWithPlayersEntity.toShortMatchDomain(
    pim: ParticipantInMatchEntity
): ParticipantInShortMatch {
    return if (secondPlayer != null) {
        ParticipantInShortDoublesMatch(
            id = participant.id,
            seed = pim.seed,
            isWinner = pim.isWinner,
            isRetired = pim.isRetired,
            firstPlayer = player.toDomain(),
            secondPlayer = secondPlayer.toDomain(),
        )
    } else {
        ParticipantInShortSinglesMatch(
            id = participant.id,
            seed = pim.seed,
            isWinner = pim.isWinner,
            isRetired = pim.isRetired,
            player = player.toDomain(),
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
