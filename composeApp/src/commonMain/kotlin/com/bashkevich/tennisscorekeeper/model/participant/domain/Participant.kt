package com.bashkevich.tennisscorekeeper.model.participant.domain

import com.bashkevich.tennisscorekeeper.model.player.domain.TennisPlayerInMatch

sealed class TennisParticipant{
    abstract val id: String
    abstract val seed: Int?
    abstract val displayName: String
}

data class SinglesParticipant(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    val player: TennisPlayerInMatch
): TennisParticipant()

data class DoublesParticipant(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    val firstPlayer: TennisPlayerInMatch,
    val secondPlayer: TennisPlayerInMatch
): TennisParticipant()