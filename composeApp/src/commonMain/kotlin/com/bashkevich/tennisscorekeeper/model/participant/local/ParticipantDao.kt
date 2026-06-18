package com.bashkevich.tennisscorekeeper.model.participant.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.bashkevich.tennisscorekeeper.model.player.local.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {

    @Transaction
    @Query("SELECT * FROM participants WHERE tournament_id = :tournamentId")
    fun getParticipantsForTournament(tournamentId: String): Flow<List<ParticipantWithPlayersEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(entity: ParticipantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(entities: List<ParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Query("DELETE FROM participants WHERE tournament_id = :tournamentId")
    suspend fun deleteParticipantsByTournament(tournamentId: String)

    @Transaction
    suspend fun replaceAllParticipantsForTournament(
        tournamentId: String,
        entities: List<ParticipantWithPlayersEntity>
    ) {
        deleteParticipantsByTournament(tournamentId)
        val playerEntities = entities.map { it.player } + entities.mapNotNull { it.secondPlayer }
        insertPlayers(playerEntities)
        insertParticipants(entities.map { it.participant })
    }
}
