package com.bashkevich.tennisscorekeeper.model.counter.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CounterRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getCounters(): LoadResult<List<CounterDto>,Throwable>{
        return runOperationCatching {
            val counters = httpClient.get("/counters").body<List<CounterDto>>()

            counters
        }
    }
}