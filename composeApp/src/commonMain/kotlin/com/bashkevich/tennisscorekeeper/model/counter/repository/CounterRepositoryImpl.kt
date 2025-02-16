package com.bashkevich.tennisscorekeeper.model.counter.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.counter.toDomain

class CounterRepositoryImpl(
    private val counterRemoteDataSource: CounterRemoteDataSource
) : CounterRepository {
    override suspend fun getCounters(): LoadResult<List<Counter>, Throwable> {
        return counterRemoteDataSource.getCounters().mapSuccess { counterDtos ->
            val counters = counterDtos.map { it.toDomain() }
            counters
        }
    }


}