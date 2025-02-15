package com.bashkevich.tennisscorekeeper.model.counter.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.post

class CounterRemoteDataSource(
    private val httpClient: HttpClient
) {
    fun doSomething(){
    }
}