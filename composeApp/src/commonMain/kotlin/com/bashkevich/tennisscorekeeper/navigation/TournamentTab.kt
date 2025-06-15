package com.bashkevich.tennisscorekeeper.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TournamentTab {
    @Serializable
    @SerialName("matches")
    data object Matches : TournamentTab()

    @Serializable
    @SerialName("participants")
    data object Participants : TournamentTab()
}

fun TournamentTab.toRouteString(): String = when (this) {
    TournamentTab.Matches -> "matches"
    TournamentTab.Participants -> "participants"
}