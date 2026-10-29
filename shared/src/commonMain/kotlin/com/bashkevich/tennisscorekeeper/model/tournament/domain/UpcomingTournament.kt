package com.bashkevich.tennisscorekeeper.model.tournament.domain

import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentApiUpcomingEntity
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentApiUpcomingDto
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import kotlinx.datetime.LocalDate

data class UpcomingTournament(
    val id: String,
    val name: String,
    val type: TournamentType,
    val dateStart: LocalDate,
)

fun TournamentApiUpcomingDto.toDomain(): UpcomingTournament? = runCatching {
    UpcomingTournament(
        id = apiId,
        name = name,
        type = TournamentType.valueOf(type),
        dateStart = startDate,
    )
}.getOrNull()

fun UpcomingTournament.toEntity() = TournamentApiUpcomingEntity(
    id = id,
    name = name,
    type = type.name,
    dateStart = dateStart.toEpochDays(),
)

fun TournamentApiUpcomingEntity.toDomain() = UpcomingTournament(
    id = id,
    name = name,
    type = TournamentType.valueOf(type),
    dateStart = LocalDate.fromEpochDays(dateStart),
)
