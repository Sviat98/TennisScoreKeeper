package com.bashkevich.tennisscorekeeper.model.tournament.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    fun getTournamentById(id: Int): Flow<TournamentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<TournamentEntity>)

    @Query("DELETE FROM tournaments")
    suspend fun deleteAllTournaments()

    @Query("UPDATE tournaments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE tournaments SET total_participants = :amount WHERE id = :id")
    suspend fun updateTotalParticipants(id: Int, amount: Int)

    @Query("UPDATE tournaments SET uncompleted_matches = uncompleted_matches + 1 WHERE id = :id")
    suspend fun incrementUncompletedMatches(id: Int)

    @Transaction
    suspend fun replaceAllTournaments(tournaments: List<TournamentEntity>) {
        deleteAllTournaments()
        insertTournaments(tournaments)
    }
}
