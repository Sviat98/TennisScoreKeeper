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

val TOURNAMENT_DEFAULT = Tournament(id = "0", name = "", type = TournamentType.SINGLES, status = TournamentStatus.NOT_STARTED)

fun TournamentDto.toDomain() =
    Tournament(id = this.id, name = this.name, type = this.type, status = this.status)
