package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantLocalDataSource
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepositoryImpl
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val participantModule = module {
    single<ParticipantLocalDataSource>()
    single<ParticipantRepositoryImpl>().bind(ParticipantRepository::class)
    single<ParticipantRemoteDataSource>()
}
