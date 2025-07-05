package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val participantModule = module {
    singleOf(::ParticipantRepositoryImpl){
        bind<ParticipantRepository>()
    }
    singleOf(::ParticipantRemoteDataSource)
}