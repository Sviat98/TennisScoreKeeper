package com.bashkevich.tennisscorekeeper.model.tournament.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentApiUpcomingDao {

    @Query("SELECT * FROM tournament_api_upcoming ORDER BY date_start ASC")
    fun getAllUpcomingTournaments(): Flow<List<TournamentApiUpcomingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingTournaments(tournaments: List<TournamentApiUpcomingEntity>)

    @Query("DELETE FROM tournament_api_upcoming")
    suspend fun deleteAllUpcomingTournaments()

    @Transaction
    suspend fun replaceAllUpcomingTournaments(tournaments: List<TournamentApiUpcomingEntity>) {
        deleteAllUpcomingTournaments()
        insertUpcomingTournaments(tournaments)
    }
}
