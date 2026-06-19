package com.bashkevich.tennisscorekeeper.model.match.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantDao
import com.bashkevich.tennisscorekeeper.model.participant.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchLocalDataSource(
    db: AppDatabase
) {
    private val matchDao: MatchDao = db.matchDao()
    private val participantDao: ParticipantDao = db.participantDao()

    fun observeMatches(tournamentId: String): Flow<List<ShortMatch>> {
        return matchDao.getMatchesForTournament(tournamentId).map { matches ->
            matches.map { it.toDomain() }
        }
    }

    suspend fun replaceMatchesForTournament(tournamentId: String, dtos: List<ShortMatchDto>) {
        val participantWithPlayers = dtos.flatMap { dto ->
            listOf(dto.firstParticipant, dto.secondParticipant).map { it.toEntity(tournamentId) }
        }

        val players = participantWithPlayers.map { it.player } +
                participantWithPlayers.mapNotNull { it.secondPlayer }

        val matches = dtos.map { it.toMatchEntity(tournamentId) }
        val sets = dtos.flatMap { it.toMatchSetEntities() }
        val games = dtos.mapNotNull { it.toMatchGameEntity() }
        val participantsInMatch = dtos.flatMap { it.toParticipantInMatchEntities() }

        participantDao.insertPlayers(players)
        participantDao.insertParticipants(participantWithPlayers.map { it.participant })
        matchDao.replaceAllMatchesForTournament(
            tournamentId = tournamentId,
            matches = matches,
            sets = sets,
            games = games,
            participantsInMatch = participantsInMatch,
        )
    }

    suspend fun insertMatch(tournamentId: String, dto: ShortMatchDto) {
        val participantWithPlayers = listOf(
            dto.firstParticipant.toEntity(tournamentId),
            dto.secondParticipant.toEntity(tournamentId),
        )

        val players = participantWithPlayers.map { it.player } +
                participantWithPlayers.mapNotNull { it.secondPlayer }

        participantDao.insertPlayers(players)
        participantDao.insertParticipants(participantWithPlayers.map { it.participant })

        matchDao.insertMatches(listOf(dto.toMatchEntity(tournamentId)))
        matchDao.insertMatchSets(dto.toMatchSetEntities())
        dto.toMatchGameEntity()?.let { matchDao.insertMatchGames(listOf(it)) }
        matchDao.insertParticipantsInMatch(dto.toParticipantInMatchEntities())
    }
}
