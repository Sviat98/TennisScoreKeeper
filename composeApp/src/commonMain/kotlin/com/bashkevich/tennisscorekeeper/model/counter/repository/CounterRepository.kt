package com.bashkevich.tennisscorekeeper.model.counter.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.counter.Counter

interface CounterRepository {
    suspend fun getCounters(): LoadResult<List<Counter>, Throwable>
}