package com.bashkevich.tennisscorekeeper.model.tournament.domain

import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentEntity
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentDto
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

data class Tournament(
    val id: String,
    val name: String,
    val type: TournamentType,
    val status: TournamentStatus,
    val regularSetTemplateId: String?,
    val decidingSetTemplateId: String,
    val themeId: String,
    val setsToWin: Int,
    val totalParticipants: Int,
    val totalMatches: Int,
    val uncompletedMatches: Int,
)

val TOURNAMENT_DEFAULT = Tournament(
    id = "0", name = "", type = TournamentType.SINGLES, status = TournamentStatus.NOT_STARTED,
    regularSetTemplateId = null, decidingSetTemplateId = "", themeId = "", setsToWin = 1,
    totalParticipants = 0, totalMatches = 0, uncompletedMatches = 0
)

fun TournamentDto.toDomain() =
    Tournament(
        id = this.id, name = this.name, type = this.type, status = this.status,
        regularSetTemplateId = this.regularSetTemplateId,
        decidingSetTemplateId = this.decidingSetTemplateId,
        themeId = this.themeId,
        setsToWin = this.setsToWin,
        totalParticipants = this.totalParticipants,
        totalMatches = this.totalMatches,
        uncompletedMatches = this.uncompletedMatches,
    )

fun TournamentEntity.toDomain() = Tournament(
    id = id,
    name = name,
    type = TournamentType.valueOf(type),
    status = TournamentStatus.valueOf(status),
    regularSetTemplateId = regularSetTemplateId,
    decidingSetTemplateId = decidingSetTemplateId,
    themeId = themeId,
    setsToWin = setsToWin,
    totalParticipants = totalParticipants,
    totalMatches = totalMatches,
    uncompletedMatches = uncompletedMatches,
)
