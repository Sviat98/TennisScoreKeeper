package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.Transactor
import androidx.room3.useWriterConnection
import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantDao
import com.bashkevich.tennisscorekeeper.model.participant.local.toEntity
import kotlinx.coroutines.flow.Flow

class MatchLocalDataSource(
    private val db: AppDatabase
) {
    private val matchDao: MatchDao = db.matchDao()
    private val participantDao: ParticipantDao = db.participantDao()


    fun observeMatches(tournamentId: Int): Flow<List<MatchWithParticipantsEntity>> {
        return matchDao.getMatchesForTournament(tournamentId)
    }

    fun observeMatchById(matchId: Int): Flow<MatchWithParticipantsEntity?> {
        return matchDao.observeMatchById(matchId)
    }

    suspend fun replaceMatchesForTournament(tournamentId: Int, dtos: List<ShortMatchDto>) {
        val participantWithPlayers = dtos.flatMap { dto ->
            listOf(dto.firstParticipant, dto.secondParticipant).map { it.toEntity(tournamentId) }
        }

        val players = participantWithPlayers.map { it.player } +
                participantWithPlayers.mapNotNull { it.secondPlayer }

        val matches = dtos.map { it.toMatchEntity(tournamentId) }
        val sets = dtos.flatMap { it.toMatchSetEntities() }
        val games = dtos.mapNotNull { it.toMatchGameEntity() }
        val participantsInMatch = dtos.flatMap { it.toParticipantInMatchEntities() }

        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE){
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
        }
    }

    suspend fun insertMatch(tournamentId: Int, dto: ShortMatchDto) {
        val participantWithPlayers = listOf(
            dto.firstParticipant.toEntity(tournamentId),
            dto.secondParticipant.toEntity(tournamentId),
        )

        val players = participantWithPlayers.map { it.player } +
                participantWithPlayers.mapNotNull { it.secondPlayer }

        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE){
                participantDao.insertPlayers(players)
                participantDao.insertParticipants(participantWithPlayers.map { it.participant })

                matchDao.insertMatch(dto.toMatchEntity(tournamentId))
                matchDao.insertMatchSets(dto.toMatchSetEntities())
                dto.toMatchGameEntity()?.let { matchDao.insertMatchGames(listOf(it)) }
                matchDao.insertParticipantsInMatch(dto.toParticipantInMatchEntities())
            }
        }
    }

    suspend fun deleteMatchesForTournament(tournamentId: Int) {
        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                matchDao.deleteMatchesByTournament(tournamentId)
            }
        }
    }

    suspend fun deleteAllMatches(){
        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE){
                matchDao.deleteAllMatches()
                participantDao.deleteAllParticipantsWithPlayers()
            }
        }
    }

    suspend fun getMatchById(matchId: Int): MatchEntity? {
        return matchDao.getMatchById(matchId)
    }

    suspend fun updateMatchDetails(
        match: MatchEntity,
        sets: List<MatchSetEntity>,
        game: MatchGameEntity?,
        participants: List<ParticipantInMatchEntity>
    ) {
        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                matchDao.updateMatch(match)
                matchDao.insertMatchSets(sets)
                if (game != null) {
                    matchDao.insertMatchGames(listOf(game))
                }
                matchDao.insertParticipantsInMatch(participants)
            }
        }
    }
}
