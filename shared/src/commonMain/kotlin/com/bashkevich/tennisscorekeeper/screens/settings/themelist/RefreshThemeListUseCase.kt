package com.bashkevich.tennisscorekeeper.screens.settings.themelist

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class RefreshThemeListUseCase(
    private val themeRepository: ThemeRepository
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun refreshThemes() {
        refreshTrigger.tryEmit(Unit)
    }

    fun fetchThemesFlow(): Flow<LoadResult<Unit, Throwable>?> = flow {
        refreshTrigger.onStart { emit(Unit) }.collect {
            emit(null)

            themeRepository.fetchThemes()
                .doOnSuccess { emit(LoadResult.Success(Unit)) }
                .doOnError { emit(LoadResult.Error(it)) }
        }
    }
}
