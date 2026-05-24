package com.bashkevich.tennisscorekeeper.model.set_template.domain

import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateEntity
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateDto

data class SetTemplate(
    val id: String,
    val name: String,
    val gamesToWin: Int,
    val hasDecidingPoint: Boolean,
    val tiebreakMode: TiebreakMode,
    val tiebreakPointsToWin: Int,
    val isRegular: Boolean,
    val isDeciding: Boolean,
)

val EMPTY_REGULAR_SET_TEMPLATE = SetTemplate(
    id = "0",
    name = "",
    gamesToWin = 0,
    hasDecidingPoint = false,
    tiebreakMode = TiebreakMode.NO,
    tiebreakPointsToWin = 0,
    isRegular = true,
    isDeciding = false
)

val EMPTY_DECIDING_SET_TEMPLATE = SetTemplate(
    id = "0",
    name = "",
    gamesToWin = 0,
    hasDecidingPoint = false,
    tiebreakMode = TiebreakMode.NO,
    tiebreakPointsToWin = 0,
    isRegular = false,
    isDeciding = true
)

fun SetTemplateDto.toDomain() = SetTemplate(
    id = this.id,
    name = this.name.replace(";", "\n"),
    gamesToWin = this.gamesToWin,
    hasDecidingPoint = this.hasDecidingPoint,
    tiebreakMode = this.tiebreakMode,
    tiebreakPointsToWin = this.tiebreakPointsToWin,
    isRegular = this.isRegular,
    isDeciding = this.isDeciding
)

fun SetTemplateEntity.toDomain() = SetTemplate(
    id = id,
    name = name.replace(";", "\n"),
    gamesToWin = gamesToWin,
    hasDecidingPoint = hasDecidingPoint,
    tiebreakMode = TiebreakMode.valueOf(tiebreakMode),
    tiebreakPointsToWin = tiebreakPointsToWin,
    isRegular = isRegular,
    isDeciding = isDeciding,
)
