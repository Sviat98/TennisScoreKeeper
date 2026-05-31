package com.bashkevich.tennisscorekeeper.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun <T> Flow<T>.onetimeStateIn(
    scope: CoroutineScope,
    initialValue: T
): kotlinx.coroutines.flow.StateFlow<T> {
    val state = MutableStateFlow(initialValue)
    var isStarted = false

    scope.launch {
        state.subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged()
            .collect { active ->
                if (active && !isStarted) {
                    isStarted = true
                    this@onetimeStateIn.collect { value ->
                        state.value = value
                    }
                }
            }
    }

    return state.asStateFlow()
}
