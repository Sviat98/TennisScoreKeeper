package com.bashkevich.tennisscorekeeper.model.set_template.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.mapSuccess
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.domain.toDomain
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateRemoteDataSource

class SetTemplateRepositoryImpl(
    private val setTemplateRemoteDataSource: SetTemplateRemoteDataSource
) : SetTemplateRepository {

    override suspend fun getSetTemplates(filter: SetTemplateTypeFilter): LoadResult<List<SetTemplate>,Throwable>{
        return setTemplateRemoteDataSource.getSetTemplates(filter).mapSuccess { setTemplateDtos ->
            setTemplateDtos.map { it.toDomain() }
        }
    }
}