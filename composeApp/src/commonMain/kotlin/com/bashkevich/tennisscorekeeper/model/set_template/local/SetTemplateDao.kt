package com.bashkevich.tennisscorekeeper.model.set_template.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SetTemplateDao {
    @Query("SELECT * FROM set_templates")
    fun getAllSetTemplates(): Flow<List<SetTemplateEntity>>

    @Query("SELECT * FROM set_templates WHERE is_regular = 1")
    fun getRegularSetTemplates(): Flow<List<SetTemplateEntity>>

    @Query("SELECT * FROM set_templates WHERE is_deciding = 1")
    fun getDecidingSetTemplates(): Flow<List<SetTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSetTemplates(templates: List<SetTemplateEntity>)

    @Query("DELETE FROM set_templates")
    suspend fun deleteAllSetTemplates()

    @Transaction
    suspend fun replaceAllSetTemplates(templates: List<SetTemplateEntity>) {
        deleteAllSetTemplates()
        insertSetTemplates(templates)
    }
}
