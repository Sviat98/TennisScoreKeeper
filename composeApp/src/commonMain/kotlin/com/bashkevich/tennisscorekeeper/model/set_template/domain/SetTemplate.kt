package com.bashkevich.tennisscorekeeper.model.set_template.domain

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

fun SetTemplateDto.toDomain() = SetTemplate(
    id = this.id,
    name = this.name,
    gamesToWin = this.gamesToWin,
    hasDecidingPoint = this.hasDecidingPoint,
    tiebreakMode = this.tiebreakMode,
    tiebreakPointsToWin = this.tiebreakPointsToWin,
    isRegular = this.isRegular,
    isDeciding = this.isDeciding
)
