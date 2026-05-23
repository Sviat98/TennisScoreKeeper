package com.bashkevich.tennisscorekeeper.model.set_template.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.doOnSuccess
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateLocalDataSource
import com.bashkevich.tennisscorekeeper.model.set_template.local.room.toEntity
import com.bashkevich.tennisscorekeeper.model.set_template.local.room.toDomain as entityToDomain
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SetTemplateRepositoryImpl(
    private val setTemplateRemoteDataSource: SetTemplateRemoteDataSource,
    private val setTemplateLocalDataSource: SetTemplateLocalDataSource
) : SetTemplateRepository {

    override suspend fun getSetTemplates(filter: SetTemplateTypeFilter): LoadResult<List<SetTemplate>,Throwable>{
        return setTemplateRemoteDataSource.getSetTemplates(filter).doOnSuccess { setTemplateDtos ->
            val entities = setTemplateDtos.map { it.toEntity() }
            setTemplateLocalDataSource.replaceAllSetTemplates(entities)
        }.mapSuccess { setTemplateDtos ->
            setTemplateDtos.map { it.toDomain() }
        }
    }

    override fun observeSetTemplates(filter: SetTemplateTypeFilter): Flow<List<SetTemplate>> {
        return setTemplateLocalDataSource.getSetTemplates(filter).map { entities ->
            entities.map { it.entityToDomain() }
        }
    }
}
