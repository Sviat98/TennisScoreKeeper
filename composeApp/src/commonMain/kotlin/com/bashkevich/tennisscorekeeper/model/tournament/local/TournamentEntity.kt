package com.bashkevich.tennisscorekeeper.model.tournament.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentDto

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
    @ColumnInfo(name = "regular_set_id")
    val regularSetTemplateId: String,
    @ColumnInfo(name = "deciding_set_id")
    val decidingSetTemplateId: String,
    @ColumnInfo(name = "theme_id")
    val themeId: String,
    @ColumnInfo(name = "sets_to_win")
    val setsToWin: Int,
    @ColumnInfo(name = "total_participants")
    val totalParticipants: Int,
    @ColumnInfo(name = "total_matches")
    val totalMatches: Int,
    @ColumnInfo(name = "uncompleted_matches")
    val uncompletedMatches: Int,
)

fun TournamentDto.toEntity() = TournamentEntity(
    id = id,
    name = name,
    type = type.name,
    status = status.name,
    regularSetTemplateId = regularSetTemplateId,
    decidingSetTemplateId = decidingSetTemplateId,
    themeId = themeId,
    setsToWin = setsToWin,
    totalParticipants = totalParticipants,
    totalMatches = totalMatches,
    uncompletedMatches = uncompletedMatches,
)

fun com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament.toEntity() = TournamentEntity(
    id = id,
    name = name,
    type = type.name,
    status = status.name,
    regularSetTemplateId = regularSetTemplateId,
    decidingSetTemplateId = decidingSetTemplateId,
    themeId = themeId,
    setsToWin = setsToWin,
    totalParticipants = totalParticipants,
    totalMatches = totalMatches,
    uncompletedMatches = uncompletedMatches,
)
