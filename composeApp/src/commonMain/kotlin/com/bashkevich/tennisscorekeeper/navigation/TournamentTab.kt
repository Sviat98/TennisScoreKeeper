package com.bashkevich.tennisscorekeeper.navigation


enum class TournamentTab {
    MATCHES, PARTICIPANTS
}

fun TournamentTab.toDisplayString(): String = this.name.lowercase()