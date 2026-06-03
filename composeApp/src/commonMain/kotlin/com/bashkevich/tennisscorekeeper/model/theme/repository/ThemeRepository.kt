package com.bashkevich.tennisscorekeeper.model.theme.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun fetchThemes(): LoadResult<Unit, Throwable>
    suspend fun fetchThemeById(id: String): LoadResult<Unit, Throwable>
    fun observeThemesFromDatabase(): Flow<List<ScoreboardTheme>>
    fun observeThemeByIdFromDatabase(id: String): Flow<ScoreboardTheme>
}
