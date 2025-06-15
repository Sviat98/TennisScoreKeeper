package com.bashkevich.tennisscorekeeper.model.set_template.repository

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter

interface SetTemplateRepository {
    suspend fun getSetTemplates(filter: SetTemplateTypeFilter): LoadResult<List<SetTemplate>, Throwable>
}