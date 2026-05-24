package com.bashkevich.tennisscorekeeper.core.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val webSocketDispatcher: CoroutineDispatcher = Dispatchers.IO