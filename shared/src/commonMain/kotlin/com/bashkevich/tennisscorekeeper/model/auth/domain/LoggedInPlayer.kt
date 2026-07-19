package com.bashkevich.tennisscorekeeper.model.auth.domain

data class LoggedInPlayer(
    val playerId: String,
    val name: String,
    val surname: String
) {
    companion object {
        fun empty() = LoggedInPlayer(
            playerId = "",
            name = "",
            surname = ""
        )
    }
}
