package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.Transactor
import androidx.room3.useWriterConnection
import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantDao
import kotlinx.coroutines.flow.Flow

class MatchLocalDataSource(
    db: Lazy<AppDatabase>,
) {
    // БД резолвится лениво (Lazy): конструируется только при первом обращении к DAO.
    // Это критично для wasmJs-scoreboard, который БД не использует: иначе при создании
    // ViewModel строилась бы Room-БД → OPFS-worker → краш "OpfsDb is not a constructor"
    // в WebView стриминговых приложений (Prism Live, Streamlabs), где OPFS недоступен.
    private val database by lazy { db.value }
    private val matchDao: MatchDao by lazy { database.matchDao() }
    private val participantDao: ParticipantDao by lazy { database.participantDao() }


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

        database.useWriterConnection {
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

        database.useWriterConnection {
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
        database.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                matchDao.deleteMatchesByTournament(tournamentId)
            }
        }
    }

    suspend fun deleteAllMatches(){
        database.useWriterConnection {
            it.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE){
                matchDao.deleteAllMatches()
                participantDao.deleteAllParticipantsWithPlayers()
            }
        }
    }
}
