package com.bashkevich.tennisscorekeeper.model.set_template.repository

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import kotlinx.coroutines.flow.Flow

interface SetTemplateRepository {
    suspend fun fetchSetTemplates(filter: SetTemplateTypeFilter): LoadResult<Unit, Throwable>
    fun fetchSetTemplatesFlow(filter: SetTemplateTypeFilter): Flow<LoadResult<Unit, Throwable>?>
    fun refreshSetTemplates()
    fun observeSetTemplates(filter: SetTemplateTypeFilter): Flow<List<SetTemplate>>
    suspend fun fetchSetTemplateById(id: String): LoadResult<Unit, Throwable>
    fun observeSetTemplateById(id: String): Flow<SetTemplate>
}
