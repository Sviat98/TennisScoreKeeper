package com.bashkevich.tennisscorekeeper.model.theme.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.file.domain.ImageFile
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeBody
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeContent
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun fetchThemes(): LoadResult<Unit, Throwable>
    fun fetchThemesFlow(): Flow<LoadResult<Unit, Throwable>?>
    fun refreshThemes()
    suspend fun fetchThemeByIdAndSaveToDb(id: Int): LoadResult<Unit, Throwable>
    suspend fun fetchThemeByIdFromNetwork(id: Int): LoadResult<ScoreboardTheme, Throwable>
    fun observeThemesFromDatabase(): Flow<List<ScoreboardTheme>>
    fun observeThemeByIdFromDatabase(id: Int): Flow<ScoreboardTheme>
    suspend fun updateTheme(id: Int, themeBody: ThemeBody): LoadResult<Unit, Throwable>
    suspend fun createTheme(themeBody: ThemeBody): LoadResult<Unit, Throwable>
    suspend fun generateThemeFromImage(image: ImageFile): LoadResult<ThemeContent, Throwable>
}
