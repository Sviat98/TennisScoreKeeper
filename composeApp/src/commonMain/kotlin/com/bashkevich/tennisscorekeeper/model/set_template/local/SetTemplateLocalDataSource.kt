package com.bashkevich.tennisscorekeeper.model.set_template.local

import com.bashkevich.tennisscorekeeper.core.local.AppDatabase
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import kotlinx.coroutines.flow.Flow

class SetTemplateLocalDataSource(
    private val db: AppDatabase
) {
    private val dao: SetTemplateDao = db.setTemplateDao()

    fun getSetTemplates(filter: SetTemplateTypeFilter): Flow<List<SetTemplateEntity>> {
        return when (filter) {
            SetTemplateTypeFilter.REGULAR -> dao.getRegularSetTemplates()
            SetTemplateTypeFilter.DECIDER -> dao.getDecidingSetTemplates()
            SetTemplateTypeFilter.ALL -> dao.getAllSetTemplates()
        }
    }

    suspend fun replaceAllSetTemplates(templates: List<SetTemplateEntity>) {
        dao.replaceAllSetTemplates(templates)
    }

    fun getSetTemplateById(id: Int): Flow<SetTemplateEntity?> {
        return dao.getSetTemplateById(id)
    }

    suspend fun insertSetTemplate(template: SetTemplateEntity) {
        dao.insertSetTemplate(template)
    }
}
