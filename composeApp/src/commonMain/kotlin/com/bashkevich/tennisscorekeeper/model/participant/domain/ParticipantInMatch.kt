package com.bashkevich.tennisscorekeeper.model.participant.domain

import com.bashkevich.tennisscorekeeper.model.player.domain.TennisPlayerInMatch

sealed class TennisParticipantInMatch{
    abstract val id: String
    abstract val seed: Int?
    abstract val displayName: String
    abstract val isServing: Boolean
    abstract val isWinner: Boolean
}

data class SinglesParticipantInMatch(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    val player: TennisPlayerInMatch
): TennisParticipantInMatch()

data class DoublesParticipantInMatch(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    val firstPlayer: TennisPlayerInMatch,
    val secondPlayer: TennisPlayerInMatch
): TennisParticipantInMatch()