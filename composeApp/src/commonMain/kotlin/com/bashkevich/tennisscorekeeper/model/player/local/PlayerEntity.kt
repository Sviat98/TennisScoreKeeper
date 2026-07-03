package com.bashkevich.tennisscorekeeper.model.player.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant
import com.bashkevich.tennisscorekeeper.model.player.remote.PlayerInParticipantDto

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "surname")
    val surname: String,
    @ColumnInfo(name = "name")
    val name: String,
)

fun PlayerInParticipantDto.toEntity() = PlayerEntity(
    id = id.toInt(),
    surname = surname,
    name = name,
)

fun PlayerEntity.toDomain() = PlayerInParticipant(
    id = id,
    surname = surname,
    name = name,
)
