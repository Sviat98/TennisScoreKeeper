package com.bashkevich.tennisscorekeeper.model.theme.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import kotlinx.coroutines.flow.Flow

class ThemeLocalDataSource(
    db: Lazy<AppDatabase>,
) {
    // Ленивая БД (см. MatchLocalDataSource): на wasmJs-scoreboard БД не нужна,
    // а её конструирование роняет OPFS-worker в WebView стриминговых приложений.
    private val database by lazy { db.value }
    private val dao: ThemeDao by lazy { database.themeDao() }

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
