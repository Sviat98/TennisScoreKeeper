package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class RefreshThemeDetailsUseCase(
    private val themeRepository: ThemeRepository,
    private val themeId: Int
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun refreshTheme() {
        refreshTrigger.tryEmit(Unit)
    }

    fun fetchThemeByIdFlow(): Flow<LoadResult<Unit, Throwable>?> = flow {
        refreshTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            themeRepository.fetchThemeById(themeId)
                .doOnSuccess { emit(LoadResult.Success(Unit)) }
                .doOnError { emit(LoadResult.Error(it)) }
        }
    }
}
