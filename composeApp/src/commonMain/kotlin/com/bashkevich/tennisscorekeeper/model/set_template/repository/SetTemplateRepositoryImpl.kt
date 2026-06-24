package com.bashkevich.tennisscorekeeper.model.set_template.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SET_TEMPLATE_DEFAULT
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateLocalDataSource
import com.bashkevich.tennisscorekeeper.model.set_template.local.toEntity
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SetTemplateRepositoryImpl(
    private val setTemplateRemoteDataSource: SetTemplateRemoteDataSource,
    private val setTemplateLocalDataSource: SetTemplateLocalDataSource
) : SetTemplateRepository {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override suspend fun fetchSetTemplates(filter: SetTemplateTypeFilter): LoadResult<Unit, Throwable> {
        return setTemplateRemoteDataSource.getSetTemplates(filter).doOnSuccess { setTemplateDtos ->
            val entities = setTemplateDtos.map { it.toEntity() }
            setTemplateLocalDataSource.replaceAllSetTemplates(entities)
        }.mapSuccess { }
    }

    override fun fetchSetTemplatesFlow(filter: SetTemplateTypeFilter): Flow<LoadResult<Unit, Throwable>?> = flow {
        refreshTrigger.onStart { emit(Unit) }.collect {
            emit(null)
            val result = setTemplateRemoteDataSource.getSetTemplates(filter)
                .doOnSuccess { setTemplateDtos ->
                    val entities = setTemplateDtos.map { it.toEntity() }
                    setTemplateLocalDataSource.replaceAllSetTemplates(entities)
                    emit(LoadResult.Success(Unit))
                }
                .doOnError {
                    emit(LoadResult.Error(it))
                }
        }
    }

    override fun refreshSetTemplates() {
        refreshTrigger.tryEmit(Unit)
    }

    override fun observeSetTemplates(filter: SetTemplateTypeFilter): Flow<List<SetTemplate>> {
        return setTemplateLocalDataSource.getSetTemplates(filter).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun fetchSetTemplateById(id: String): LoadResult<Unit, Throwable> {
        return setTemplateRemoteDataSource.getSetTemplateById(id)
            .doOnSuccess { dto ->
                setTemplateLocalDataSource.insertSetTemplate(dto.toEntity())
            }
            .mapSuccess { }
    }

    override fun observeSetTemplateById(id: String): Flow<SetTemplate> {
        return setTemplateLocalDataSource.getSetTemplateById(id).map { entity ->
            entity?.toDomain() ?: SET_TEMPLATE_DEFAULT
        }
    }
}
