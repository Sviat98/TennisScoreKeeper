package com.bashkevich.tennisscorekeeper.screens.matchdetails

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * Перезапрашиваемый flow загрузки темы матча в БД: /themes/{id} → fetchThemeById.
 * Владеет retry-триггером (первый запрос — при подписке через onStart,
 * повторный — через [refresh]). Паттерн как у RefreshThemeDetailsUseCase.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MatchDetailsRefreshThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val themeIdFlow: Flow<Int>,
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun refresh() {
        refreshTrigger.tryEmit(Unit)
    }

    fun fetchFlow(): Flow<LoadResult<Unit, Throwable>?> = themeIdFlow.flatMapLatest { id ->
        flow {
            refreshTrigger.onStart { emit(Unit) }.collect {
                emit(null)
                emit(themeRepository.fetchThemeById(id))
            }
        }
    }
}
