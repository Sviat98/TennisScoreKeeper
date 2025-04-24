package com.bashkevich.tennisscorekeeper.model.match

data class SimpleMatch(
    val id: String,
    val firstPlayer: String,
    val secondPlayer: String,
    val status: String
)
