package com.bashkevich.tennisscorekeeper.screens.scoreboard

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * Перезапрашиваемый flow загрузки темы матча напрямую из сети в память
 * (без БД): /themes/{id} → fetchThemeByIdFromNetwork.
 * Владеет retry-триггером (первый запрос — при подписке через onStart,
 * повторный — через [refresh]).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScoreboardRefreshThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val themeIdFlow: Flow<Int>,
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun refresh() {
        refreshTrigger.tryEmit(Unit)
    }

    fun fetchFlow(): Flow<LoadResult<ScoreboardTheme, Throwable>?> = themeIdFlow.flatMapLatest { id ->
        flow {
            refreshTrigger.onStart { emit(Unit) }.collect {
                emit(null)
                emit(themeRepository.fetchThemeByIdFromNetwork(id))
            }
        }
    }
}
