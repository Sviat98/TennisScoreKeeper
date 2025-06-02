package com.bashkevich.tennisscorekeeper.model.tournament.domain

import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentDto
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

data class Tournament(
    val id: String,
    val name: String,
    val type: TournamentType,
    val status: TournamentStatus,
)

fun TournamentDto.toDomain() =
    Tournament(id = this.id, name = this.name, type = this.type, status = this.status)
