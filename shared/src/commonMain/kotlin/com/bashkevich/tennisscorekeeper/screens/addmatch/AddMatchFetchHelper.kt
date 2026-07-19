package com.bashkevich.tennisscorekeeper.screens.addmatch

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class AddMatchFetchHelper(
    private val themeRepository: ThemeRepository,
    private val setTemplateRepository: SetTemplateRepository,
) {
    private val _themeTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _regularSetTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _decidingSetTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun observeThemeByIdFromNetwork(id: Int): Flow<LoadResult<Unit, Throwable>?> = flow {
        _themeTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            emit(themeRepository.fetchThemeByIdAndSaveToDb(id))
        }
    }

    fun observeRegularSetByIdFromNetwork(id: Int?): Flow<LoadResult<Unit, Throwable>?> = flow {
        id?.let {
            _regularSetTrigger.onStart { emit(Unit) }.collect {
                emit(null)
                emit(setTemplateRepository.fetchSetTemplateById(id))
            }
        }
    }

    fun observeDecidingSetByIdFromNetwork(id: Int): Flow<LoadResult<Unit, Throwable>?> = flow {
        _decidingSetTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            emit(setTemplateRepository.fetchSetTemplateById(id))
        }
    }

    fun retryTheme(id: Int) = _themeTrigger.tryEmit(Unit)
    fun retryRegularSet(id: Int) = _regularSetTrigger.tryEmit(Unit)
    fun retryDecidingSet(id: Int) = _decidingSetTrigger.tryEmit(Unit)
}
