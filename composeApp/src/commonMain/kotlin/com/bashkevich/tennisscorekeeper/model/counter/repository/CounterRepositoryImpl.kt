package com.bashkevich.tennisscorekeeper.model.counter.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterDeltaDto
import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.counter.toDomain
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class CounterRepositoryImpl(
    private val counterRemoteDataSource: CounterRemoteDataSource
) : CounterRepository {
    override suspend fun getCounters(): LoadResult<List<Counter>, Throwable> {
        return counterRemoteDataSource.getCounters().mapSuccess { counterDtos ->
            val counters = counterDtos.map { it.toDomain() }
            counters
        }
    }

    override suspend fun updateCounterValue(counterId: String,delta: Int){
        val counterDeltaDto = CounterDeltaDto(delta)

        counterRemoteDataSource.updateCounterValue(counterId,counterDeltaDto)
    }

    override suspend fun closeSession() {
        counterRemoteDataSource.closeSession()
    }

    override fun connectToCounterUpdates(counterId: String) {
        counterRemoteDataSource.connectToCounterUpdates(counterId)
    }

    override fun observeCounterUpdates() =
        counterRemoteDataSource.observeCounterUpdates()
            .map { result -> result.mapSuccess { counterDto -> counterDto.toDomain() } }


}