package com.bashkevich.tennisscorekeeper.model.theme.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeLocalDataSource
import com.bashkevich.tennisscorekeeper.model.theme.local.toEntity
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.theme.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepositoryImpl(
    private val themeRemoteDataSource: ThemeRemoteDataSource,
    private val themeLocalDataSource: ThemeLocalDataSource,
) : ThemeRepository {

    override suspend fun fetchThemes(): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.getThemes().doOnSuccess { themeDtos ->
            val entities = themeDtos.map { it.toEntity() }
            themeLocalDataSource.replaceAllThemes(entities)
        }.mapSuccess { }
    }

    override suspend fun fetchThemeById(id: String): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.getThemeById(id).doOnSuccess { themeDto ->
            themeLocalDataSource.insertTheme(themeDto.toEntity())
        }.mapSuccess { }
    }

    override fun observeThemesFromDatabase(): Flow<List<ScoreboardTheme>> {
        return themeLocalDataSource.getThemes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeThemeByIdFromDatabase(id: String): Flow<ScoreboardTheme> {
        return themeLocalDataSource.getThemeById(id).map { entity ->
            entity?.toDomain() ?: ScoreboardTheme.DEFAULT
        }
    }
}
