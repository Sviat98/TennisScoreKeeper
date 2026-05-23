package com.bashkevich.tennisscorekeeper.model.theme.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.theme.CounterTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun fetchThemes(): LoadResult<Unit, Throwable>
    suspend fun fetchThemeById(id: String): LoadResult<Unit, Throwable>
    suspend fun observeThemesFromDatabase(): Flow<List<CounterTheme>>
    suspend fun observeThemeByIdFromDatabase(id: String): Flow<CounterTheme>
}
