package com.bashkevich.tennisscorekeeper.model.match.domain

import com.bashkevich.tennisscorekeeper.model.match.remote.PlayerInDoublesMatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.PlayerInMatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.PlayerInSinglesMatchDto

sealed class TennisPlayer{
    abstract val id: String
    abstract val surname: String
    abstract val name: String
}

data class SinglesPlayer(
    override val id: String,
    override val surname: String,
    override val name: String,
) : TennisPlayer()

data class DoublesPlayer(
    override val id: String,
    override val surname: String,
    override val name: String,
    val isServing: Boolean,
): TennisPlayer()

fun PlayerInMatchDto.toDomain(): TennisPlayer = when(this){
    is PlayerInSinglesMatchDto -> SinglesPlayer(id =this.id, surname = this.surname, name = this.name)
    is PlayerInDoublesMatchDto -> DoublesPlayer(id =this.id, surname = this.surname, name = this.name, isServing = this.isServing)
}