package com.bashkevich.tennisscorekeeper.model.theme.local

import com.bashkevich.tennisscorekeeper.core.AppDatabase
import com.bashkevich.tennisscorekeeper.model.theme.local.room.ThemeDao
import com.bashkevich.tennisscorekeeper.model.theme.local.room.ThemeEntity as RoomThemeEntity
import kotlinx.coroutines.flow.Flow

class ThemeLocalDataSource(
    private val db: AppDatabase
) {
    private val dao: ThemeDao = db.themeDao()

    fun getThemes(): Flow<List<RoomThemeEntity>> {
        return dao.getAllThemes()
    }

    fun getThemeById(id: String): Flow<RoomThemeEntity?> {
        return dao.getThemeById(id)
    }

    suspend fun insertTheme(theme: RoomThemeEntity) {
        dao.insertTheme(theme)
    }

    suspend fun replaceAllThemes(themes: List<RoomThemeEntity>) {
        dao.deleteAllThemes()
        dao.insertThemes(themes)
    }
}
