package com.bashkevich.tennisscorekeeper.model.theme.remote

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

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

    suspend fun updateTheme(id: String, body: ThemeBody): LoadResult<ThemeDto, Throwable> {
        return runOperationCatching {
            httpClient.put("/themes/$id") {
                setBody(body)
            }.body<ThemeDto>()
        }
    }
}
