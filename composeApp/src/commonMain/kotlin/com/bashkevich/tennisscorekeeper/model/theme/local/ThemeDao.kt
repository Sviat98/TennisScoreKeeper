package com.bashkevich.tennisscorekeeper.model.theme.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {
    @Query("SELECT * FROM scoreboard_theme")
    fun getAllThemes(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM scoreboard_theme WHERE id = :id")
    fun getThemeById(id: String): Flow<ThemeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: ThemeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<ThemeEntity>)

    @Query("DELETE FROM scoreboard_theme")
    suspend fun deleteAllThemes()

    @Transaction
    suspend fun replaceAllThemes(themes: List<ThemeEntity>) {
        deleteAllThemes()
        insertThemes(themes)
    }
}