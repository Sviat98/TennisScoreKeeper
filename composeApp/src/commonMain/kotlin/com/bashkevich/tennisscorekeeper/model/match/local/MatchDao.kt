package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Transaction
    @Query("SELECT * FROM matches WHERE tournament_id = :tournamentId ORDER BY id DESC")
    fun getMatchesForTournament(tournamentId: String): Flow<List<MatchWithParticipantsEntity>>

    @Transaction
    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    fun observeMatchById(matchId: String): Flow<MatchWithParticipantsEntity?>

    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    suspend fun getMatchById(matchId: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(entities: List<MatchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(entity: MatchEntity)

    @Update
    suspend fun updateMatch(entity: MatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchSets(entities: List<MatchSetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchGames(entities: List<MatchGameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipantsInMatch(entities: List<ParticipantInMatchEntity>)

    @Query("DELETE FROM matches WHERE tournament_id = :tournamentId")
    suspend fun deleteMatchesByTournament(tournamentId: String)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()

    @Transaction
    suspend fun replaceAllMatchesForTournament(
        tournamentId: String,
        matches: List<MatchEntity>,
        sets: List<MatchSetEntity>,
        games: List<MatchGameEntity>,
        participantsInMatch: List<ParticipantInMatchEntity>,
    ) {
        deleteMatchesByTournament(tournamentId)
        insertMatches(matches)
        insertMatchSets(sets)
        insertMatchGames(games)
        insertParticipantsInMatch(participantsInMatch)
    }
}
