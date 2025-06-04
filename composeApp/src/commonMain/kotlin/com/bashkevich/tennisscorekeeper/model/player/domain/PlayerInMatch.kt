package com.bashkevich.tennisscorekeeper.model.player.domain

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInMatchDto
import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInSinglesMatchDto

sealed class TennisPlayerInMatch{
    abstract val id: String
    abstract val surname: String
    abstract val name: String
}

data class SinglesPlayerInMatch(
    override val id: String,
    override val surname: String,
    override val name: String,
) : TennisPlayerInMatch()

data class DoublesPlayerInMatch(
    override val id: String,
    override val surname: String,
    override val name: String,
    val isServing: Boolean,
): TennisPlayerInMatch()

fun PlayerInMatchDto.toDomain(): TennisPlayerInMatch = when(this) {
    is PlayerInSinglesMatchDto -> SinglesPlayerInMatch(
        id = this.id,
        surname = this.surname,
        name = this.name,
    )
    is PlayerInDoublesMatchDto -> DoublesPlayerInMatch(
        id = this.id,
        surname = this.surname,
        name = this.name,
        isServing = this.isServing
    )
}