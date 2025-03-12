package com.bashkevich.tennisscorekeeper.model.counter.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import com.bashkevich.tennisscorekeeper.model.counter.remote.AddCounterBody
import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterDeltaDto
import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.counter.toDomain
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class CounterRepositoryImpl(
    private val counterRemoteDataSource: CounterRemoteDataSource
) : CounterRepository {

    private val _newCounter = MutableSharedFlow<AddCounterBody>(replay = 1)

    override suspend fun getCounters(): LoadResult<List<Counter>, Throwable> {
        return counterRemoteDataSource.getCounters().mapSuccess { counterDtos ->
            val counters = counterDtos.map { it.toDomain() }
            counters
        }
    }

    override suspend fun addCounter(addCounterBody: AddCounterBody): LoadResult<Counter, Throwable>{
        return counterRemoteDataSource.addCounter(addCounterBody).mapSuccess { counterDto ->
            counterDto.toDomain()
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

    override fun emitNewCounter(addCounterBody: AddCounterBody) {
        _newCounter.tryEmit(addCounterBody)
    }

    override fun observeNewCounter() = _newCounter.asSharedFlow()


}