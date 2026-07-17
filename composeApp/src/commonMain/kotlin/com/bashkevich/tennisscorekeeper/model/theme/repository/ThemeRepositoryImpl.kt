package com.bashkevich.tennisscorekeeper.model.theme.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeLocalDataSource
import com.bashkevich.tennisscorekeeper.model.theme.local.toEntity
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeBody
import com.bashkevich.tennisscorekeeper.model.theme.remote.ThemeRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.theme.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.theme.domain.toThemeBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ThemeRepositoryImpl(
    private val themeRemoteDataSource: ThemeRemoteDataSource,
    private val themeLocalDataSource: ThemeLocalDataSource,
) : ThemeRepository {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override suspend fun fetchThemes(): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.getThemes().doOnSuccess { themeDtos ->
            val entities = themeDtos.map { it.toEntity() }
            themeLocalDataSource.replaceAllThemes(entities)
        }.mapSuccess { }
    }

    override fun fetchThemesFlow(): Flow<LoadResult<Unit, Throwable>?> = flow {
        refreshTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            val result = themeRemoteDataSource.getThemes()
                .doOnSuccess { themeDtos ->
                    val entities = themeDtos.map { it.toEntity() }
                    themeLocalDataSource.replaceAllThemes(entities)
                    emit(LoadResult.Success(Unit))
                }
                .doOnError {
                    emit(LoadResult.Error(it))
                }
        }
    }

    override fun refreshThemes() {
        refreshTrigger.tryEmit(Unit)
    }

    override suspend fun fetchThemeById(id: Int): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.getThemeById(id.toString()).doOnSuccess { themeDto ->
            themeLocalDataSource.insertTheme(themeDto.toEntity())
        }.mapSuccess { }
    }

    override suspend fun fetchThemeByIdFromNetwork(id: Int): LoadResult<ScoreboardTheme, Throwable> {
        return themeRemoteDataSource.getThemeById(id.toString()).mapSuccess { it.toDomain() }
    }

    override fun observeThemesFromDatabase(): Flow<List<ScoreboardTheme>> {
        return themeLocalDataSource.getThemes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeThemeByIdFromDatabase(id: Int): Flow<ScoreboardTheme> {
        return themeLocalDataSource.getThemeById(id).map { entity ->
            entity?.toDomain() ?: ScoreboardTheme.DEFAULT
        }
    }

    override suspend fun updateTheme(id: Int, themeBody: ThemeBody): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.updateTheme(id.toString(), themeBody).doOnSuccess { themeDto ->
            themeLocalDataSource.insertTheme(themeDto.toEntity())
        }.mapSuccess { }
    }
}
