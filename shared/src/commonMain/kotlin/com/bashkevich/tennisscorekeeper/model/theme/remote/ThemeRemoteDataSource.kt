package com.bashkevich.tennisscorekeeper.model.theme.remote

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.runOperationCatching
import com.bashkevich.tennisscorekeeper.model.file.domain.ImageFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

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

    suspend fun createTheme(body: ThemeBody): LoadResult<ThemeDto, Throwable> {
        return runOperationCatching {
            httpClient.post("/themes") {
                setBody(body)
            }.body<ThemeDto>()
        }
    }

    suspend fun generateThemeFromImage(image: ImageFile): LoadResult<ThemeContent, Throwable> {
        val contentType = if (image.name.endsWith(".png", ignoreCase = true)) {
            "image/png"
        } else {
            "image/jpeg"
        }
        return runOperationCatching {
            httpClient.post("/themes/ai") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("image", image.content, Headers.build {
                                append(HttpHeaders.ContentType, contentType)
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${image.name}\""
                                )
                            })
                        }
                    )
                )
            }.body<ThemeContent>()
        }
    }
}
