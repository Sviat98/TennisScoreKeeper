package com.bashkevich.tennisscorekeeper.model.set_template.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class SetTemplateRemoteDataSource(
    private val httpClient: HttpClient
) {

    suspend fun getSetTemplates(filter: SetTemplateTypeFilter): LoadResult<List<SetTemplateDto>, Throwable>{
        return runOperationCatching {
            val setTemplates = httpClient.get("/set-templates"){
                parameter("type",filter)
            }.body<List<SetTemplateDto>>()

            setTemplates
        }
    }
}