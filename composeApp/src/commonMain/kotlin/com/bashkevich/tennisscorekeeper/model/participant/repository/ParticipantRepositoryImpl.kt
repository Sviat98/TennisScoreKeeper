package com.bashkevich.tennisscorekeeper.model.participant.repository

import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantRemoteDataSource

class ParticipantRepositoryImpl(
    private val participantRemoteDataSource: ParticipantRemoteDataSource
) : ParticipantRepository {
}