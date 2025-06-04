package com.bashkevich.tennisscorekeeper.model.player.domain

import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInParticipantDto

data class PlayerInParticipant(
    val id: String,
    val surname: String,
    val name: String,
)

fun PlayerInParticipantDto.toDomain() = PlayerInParticipant(
    id = this.id,
    surname = this.surname,
    name = this.name
)