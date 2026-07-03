package com.bashkevich.tennisscorekeeper.model.theme.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import kotlinx.coroutines.flow.Flow

class ThemeLocalDataSource(
    private val db: AppDatabase
) {
    private val dao: ThemeDao = db.themeDao()

    fun getThemes(): Flow<List<ThemeEntity>> {
        return dao.getAllThemes()
    }

    fun getThemeById(id: Int): Flow<ThemeEntity?> {
        return dao.getThemeById(id)
    }

    suspend fun insertTheme(theme: ThemeEntity) {
        dao.insertTheme(theme)
    }

    suspend fun replaceAllThemes(themes: List<ThemeEntity>) {
        dao.replaceAllThemes(themes)
    }
}
