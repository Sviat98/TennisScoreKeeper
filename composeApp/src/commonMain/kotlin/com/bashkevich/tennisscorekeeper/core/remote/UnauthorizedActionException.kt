package com.bashkevich.tennisscorekeeper.core.remote

class UnauthorizedActionException(
    message: String = "You need to login for this action"
) : UnauthorizedException(message)
