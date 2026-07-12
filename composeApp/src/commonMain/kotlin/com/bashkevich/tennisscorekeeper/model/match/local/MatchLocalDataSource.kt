package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.Transactor
import androidx.room3.useWriterConnection
import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantDao
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

    suspend fun replaceMatchesForTournament(tournamentId: Int, matches: List<MatchWithParticipantsEntity>) {
        val participantWithPlayers = matches.flatMap { match ->
            listOf(match.firstParticipant.participant, match.secondParticipant.participant)
        }

        val players = participantWithPlayers.map { it.player } +
                participantWithPlayers.mapNotNull { it.secondPlayer }

        val matchEntities = matches.map { it.match }
        val sets = matches.flatMap { it.sets }
        val games = matches.mapNotNull { it.game }
        val participantsInMatch = matches.flatMap { match ->
            listOf(match.firstParticipant.participantInMatch, match.secondParticipant.participantInMatch)
        }

        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE){
                participantDao.insertPlayers(players)
                participantDao.insertParticipants(participantWithPlayers.map { it.participant })
                matchDao.replaceAllMatchesForTournament(
                    tournamentId = tournamentId,
                    matches = matchEntities,
                    sets = sets,
                    games = games,
                    participantsInMatch = participantsInMatch,
                )
            }
        }
    }

    suspend fun insertMatch(match: MatchWithParticipantsEntity) {
        val participantWithPlayers = listOf(
            match.firstParticipant.participant,
            match.secondParticipant.participant,
        )

        val players = participantWithPlayers.map { it.player } +
                participantWithPlayers.mapNotNull { it.secondPlayer }

        db.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE){
                participantDao.insertPlayers(players)
                participantDao.insertParticipants(participantWithPlayers.map { it.participant })

                matchDao.insertMatch(match.match)
                matchDao.insertMatchSets(match.sets)
                match.game?.let { matchDao.insertMatchGame(it) }
                matchDao.insertParticipantsInMatch(listOf(
                    match.firstParticipant.participantInMatch,
                    match.secondParticipant.participantInMatch,
                ))
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
}
