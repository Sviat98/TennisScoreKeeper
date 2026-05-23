package com.bashkevich.tennisscorekeeper.model.theme.local.room

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {
    @Query("SELECT * FROM counter_theme")
    fun getAllThemes(): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM counter_theme WHERE id = :id")
    fun getThemeById(id: String): Flow<ThemeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: ThemeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<ThemeEntity>)

    @Query("DELETE FROM counter_theme")
    suspend fun deleteAllThemes()
}
