package com.bashkevich.tennisscorekeeper.screens.addmatch

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SET_TEMPLATE_DEFAULT
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
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

    /** Для id дефолтной темы сетевой вызов не выполняется — сразу Success. */
    fun observeThemeByIdFromNetwork(id: Int): Flow<LoadResult<Unit, Throwable>?> = flow {
        _themeTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            if (id == ScoreboardTheme.DEFAULT.id) {
                emit(LoadResult.Success(Unit))
            } else {
                emit(themeRepository.fetchThemeByIdAndSaveToDb(id))
            }
        }
    }

    /** Для id дефолтного сет-темплейта сетевой вызов не выполняется — сразу Success. */
    fun observeRegularSetByIdFromNetwork(id: Int?): Flow<LoadResult<Unit, Throwable>?> = flow {
        id?.let {
            _regularSetTrigger.onStart { emit(Unit) }.collect {
                emit(null)
                if (id == SET_TEMPLATE_DEFAULT.id) {
                    emit(LoadResult.Success(Unit))
                } else {
                    emit(setTemplateRepository.fetchSetTemplateById(id))
                }
            }
        }
    }

    /** Для id дефолтного сет-темплейта сетевой вызов не выполняется — сразу Success. */
    fun observeDecidingSetByIdFromNetwork(id: Int): Flow<LoadResult<Unit, Throwable>?> = flow {
        _decidingSetTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            if (id == SET_TEMPLATE_DEFAULT.id) {
                emit(LoadResult.Success(Unit))
            } else {
                emit(setTemplateRepository.fetchSetTemplateById(id))
            }
        }
    }

    fun retryTheme(id: Int) = _themeTrigger.tryEmit(Unit)
    fun retryRegularSet(id: Int) = _regularSetTrigger.tryEmit(Unit)
    fun retryDecidingSet(id: Int) = _decidingSetTrigger.tryEmit(Unit)
}
