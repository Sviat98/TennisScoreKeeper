package com.bashkevich.tennisscorekeeper.model.tournament.local.room

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentDto
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "status")
    val status: String,
)

fun TournamentEntity.toDomain() = Tournament(
    id = id,
    name = name,
    type = TournamentType.valueOf(type),
    status = TournamentStatus.valueOf(status),
)

fun TournamentDto.toEntity() = TournamentEntity(
    id = id,
    name = name,
    type = type.name,
    status = status.name,
)
