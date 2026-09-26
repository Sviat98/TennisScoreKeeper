package com.bashkevich.tennisscorekeeper.model.auth.domain

data class LoggedInUser(
    val userId: String,
    val name: String,
    val surname: String
) {
    companion object {
        fun empty() = LoggedInUser(
            userId = "",
            name = "",
            surname = ""
        )
    }
}
