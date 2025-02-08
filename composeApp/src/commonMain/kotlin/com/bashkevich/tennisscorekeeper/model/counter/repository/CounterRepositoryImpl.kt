package com.bashkevich.tennisscorekeeper.model.counter.repository

import com.bashkevich.tennisscorekeeper.model.counter.remote.CounterRemoteDataSource

class CounterRepositoryImpl(
    private val counterRemoteDataSource: CounterRemoteDataSource
) : CounterRepository{
}