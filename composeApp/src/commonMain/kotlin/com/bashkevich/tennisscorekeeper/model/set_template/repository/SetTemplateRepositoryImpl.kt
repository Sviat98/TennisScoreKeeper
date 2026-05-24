package com.bashkevich.tennisscorekeeper.model.set_template.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.remote.mapSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateLocalDataSource
import com.bashkevich.tennisscorekeeper.model.set_template.local.toEntity
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SetTemplateRepositoryImpl(
    private val setTemplateRemoteDataSource: SetTemplateRemoteDataSource,
    private val setTemplateLocalDataSource: SetTemplateLocalDataSource
) : SetTemplateRepository {

    override suspend fun fetchSetTemplates(filter: SetTemplateTypeFilter): LoadResult<Unit, Throwable> {
        return setTemplateRemoteDataSource.getSetTemplates(filter).doOnSuccess { setTemplateDtos ->
            val entities = setTemplateDtos.map { it.toEntity() }
            setTemplateLocalDataSource.replaceAllSetTemplates(entities)
        }.mapSuccess { }
    }

    override fun observeSetTemplates(filter: SetTemplateTypeFilter): Flow<List<SetTemplate>> {
        return setTemplateLocalDataSource.getSetTemplates(filter).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
