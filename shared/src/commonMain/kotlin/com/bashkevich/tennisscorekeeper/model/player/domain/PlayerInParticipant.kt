package com.bashkevich.tennisscorekeeper.model.player.domain

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerDto

data class PlayerInParticipant(
    val id: Int,
    val surname: String,
    val name: String,
)

fun PlayerDto.toDomain() = PlayerInParticipant(
    id = this.id.toInt(),
    surname = this.surname,
    name = this.name
)