package com.bashkevich.tennisscorekeeper.model.counter.remote

import io.ktor.client.HttpClient

class CounterRemoteDataSource(
    private val httpClient: HttpClient
) {
}