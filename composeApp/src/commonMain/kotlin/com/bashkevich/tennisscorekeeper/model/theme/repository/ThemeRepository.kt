package com.bashkevich.tennisscorekeeper.model.theme.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun fetchThemes(): LoadResult<Unit, Throwable>
    fun fetchThemesFlow(): Flow<LoadResult<Unit, Throwable>?>
    fun refreshThemes()
    suspend fun fetchThemeById(id: Int): LoadResult<Unit, Throwable>
    fun observeThemesFromDatabase(): Flow<List<ScoreboardTheme>>
    fun observeThemeByIdFromDatabase(id: Int): Flow<ScoreboardTheme>
}
