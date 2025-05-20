package com.bashkevich.tennisscorekeeper.model.match.domain

sealed class TennisParticipant{
    abstract val id: String
    abstract val seed: Int?
    abstract val displayName: String
    abstract val isServing: Boolean
    abstract val isWinner: Boolean
}

data class SinglesParticipant(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    val player: TennisPlayer
): TennisParticipant()

data class DoublesParticipant(
    override val id: String,
    override val seed: Int?,
    override val displayName: String,
    override val isServing: Boolean,
    override val isWinner: Boolean,
    val firstPlayer: TennisPlayer,
    val secondPlayer: TennisPlayer
): TennisParticipant()