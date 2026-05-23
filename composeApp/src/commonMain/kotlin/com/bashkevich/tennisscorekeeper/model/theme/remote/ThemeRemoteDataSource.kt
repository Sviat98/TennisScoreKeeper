package com.bashkevich.tennisscorekeeper.model.theme.remote

import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ThemeRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getThemes(): LoadResult<List<ThemeDto>, Throwable> {
        return runOperationCatching {
            httpClient.get("/themes").body<List<ThemeDto>>()
        }
    }

    suspend fun getThemeById(id: String): LoadResult<ThemeDto, Throwable> {
        return runOperationCatching {
            httpClient.get("/themes/$id").body<ThemeDto>()
        }
    }
}
