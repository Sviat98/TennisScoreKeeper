package com.bashkevich.tennisscorekeeper.model.set_template.local

import com.bashkevich.tennisscorekeeper.core.AppDatabase
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.local.room.SetTemplateDao
import com.bashkevich.tennisscorekeeper.model.set_template.local.room.SetTemplateEntity
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
        dao.deleteAllSetTemplates()
        dao.insertSetTemplates(templates)
    }
}
