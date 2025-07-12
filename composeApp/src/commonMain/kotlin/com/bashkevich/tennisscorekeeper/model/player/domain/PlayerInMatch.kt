package com.bashkevich.tennisscorekeeper.model.player.domain

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInMatchDto
import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInSinglesMatchDto

sealed class TennisPlayerInMatch{
    abstract val id: String
    abstract val surname: String
    abstract val name: String
}

data class PlayerInSinglesMatch(
    override val id: String,
    override val surname: String,
    override val name: String,
) : TennisPlayerInMatch()

data class PlayerInDoublesMatch(
    override val id: String,
    override val surname: String,
    override val name: String,
    val isServingNow: Boolean,
    val isServingNext: Boolean,
    ): TennisPlayerInMatch()

fun PlayerInMatchDto.toDomain(): TennisPlayerInMatch = when(this) {
    is PlayerInSinglesMatchDto -> PlayerInSinglesMatch(
        id = this.id,
        surname = this.surname,
        name = this.name,
    )
    is PlayerInDoublesMatchDto -> PlayerInDoublesMatch(
        id = this.id,
        surname = this.surname,
        name = this.name,
        isServingNow = this.isServingNow,
        isServingNext = this.isServingNext
    )
}