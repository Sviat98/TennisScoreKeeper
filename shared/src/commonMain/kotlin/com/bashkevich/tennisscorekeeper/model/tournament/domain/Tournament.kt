package com.bashkevich.tennisscorekeeper.model.tournament.domain

import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentEntity
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentDto
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

data class Tournament(
    val id: Int,
    val name: String,
    val type: TournamentType,
    val status: TournamentStatus,
    val regularSetTemplateId: Int?,
    val decidingSetTemplateId: Int,
    val themeId: Int,
    val setsToWin: Int,
    val totalParticipants: Int,
    val totalMatches: Int,
    val uncompletedMatches: Int,
)

val TOURNAMENT_DEFAULT = Tournament(
    id = 0, name = "", type = TournamentType.SINGLES, status = TournamentStatus.NOT_STARTED,
    regularSetTemplateId = null, decidingSetTemplateId = 0, themeId = 0, setsToWin = 1,
    totalParticipants = 0, totalMatches = 0, uncompletedMatches = 0
)

fun TournamentDto.toDomain() =
    Tournament(
        id = this.id.toInt(), name = this.name, type = this.type, status = this.status,
        regularSetTemplateId = this.regularSetTemplateId?.toInt(),
        decidingSetTemplateId = this.decidingSetTemplateId.toInt(),
        themeId = this.themeId.toInt(),
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
