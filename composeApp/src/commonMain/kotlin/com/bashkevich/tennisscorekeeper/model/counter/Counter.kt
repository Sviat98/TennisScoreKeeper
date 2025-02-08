package com.bashkevich.tennisscorekeeper.model.counter

import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterDto

data class Counter(
    val id: String,
    val name: String,
    val value: Int
)
val COUNTER_DEFAULT = Counter("0","Counter 0",-1)

val COUNTERS = listOf(
    Counter(id = "1", name = "Counter 1", value = 1),
    Counter(id = "2", name = "Counter 2", value = 2),
    Counter(id = "3", name = "Counter 3", value = 3)
)

fun CounterDto.toDomain() = Counter(id, name, value)
