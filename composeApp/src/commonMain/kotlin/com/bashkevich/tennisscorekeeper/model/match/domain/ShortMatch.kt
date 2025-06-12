package com.bashkevich.tennisscorekeeper.model.match.domain

import com.bashkevich.tennisscorekeeper.model.match.remote.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDomain

data class ShortMatch(
    val id: String,
    val firstParticipant: ParticipantInShortMatch,
    val secondParticipant: ParticipantInShortMatch,
    val status: MatchStatus,
    val finalScore: List<TennisSet>,
)

fun ShortMatchDto.toDomain() = ShortMatch(
    id = this.id,
    firstParticipant = this.firstParticipant.toDomain(),
    secondParticipant = this.secondParticipant.toDomain(),
    status = this.status,
    finalScore = this.finalScore.map { it.toDomain() }
)