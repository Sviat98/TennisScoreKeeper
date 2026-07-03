package com.bashkevich.tennisscorekeeper.model.set_template.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateDto

@Entity(tableName = "set_templates")
data class SetTemplateEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "games_to_win")
    val gamesToWin: Int,
    @ColumnInfo(name = "has_deciding_point")
    val hasDecidingPoint: Boolean,
    @ColumnInfo(name = "tiebreak_mode")
    val tiebreakMode: String,
    @ColumnInfo(name = "tiebreak_points_to_win")
    val tiebreakPointsToWin: Int,
    @ColumnInfo(name = "is_regular")
    val isRegular: Boolean,
    @ColumnInfo(name = "is_deciding")
    val isDeciding: Boolean,
)

fun SetTemplateDto.toEntity() = SetTemplateEntity(
    id = id.toInt(),
    name = name,
    gamesToWin = gamesToWin,
    hasDecidingPoint = hasDecidingPoint,
    tiebreakMode = tiebreakMode.name,
    tiebreakPointsToWin = tiebreakPointsToWin,
    isRegular = isRegular,
    isDeciding = isDeciding,
)
