package com.bashkevich.tennisscorekeeper.core.remote

import kotlinx.coroutines.CoroutineDispatcher

internal expect val webSocketDispatcher: CoroutineDispatcher
