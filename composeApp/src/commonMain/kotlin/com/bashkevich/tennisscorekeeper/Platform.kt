package com.bashkevich.tennisscorekeeper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform