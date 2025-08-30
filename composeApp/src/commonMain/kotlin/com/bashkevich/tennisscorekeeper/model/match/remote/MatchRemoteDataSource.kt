package com.bashkevich.tennisscorekeeper.model.match.remote

import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.core.LoadResult
import com.bashkevich.tennisscorekeeper.core.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.runOperationCatching
import com.bashkevich.tennisscorekeeper.core.webSocketDispatcher
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ChangeScoreBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatusBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.RetiredParticipantBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeInPairBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MatchRemoteDataSource(
    private val httpClient: HttpClient
) {
    private var webSocketSession: DefaultClientWebSocketSession? = null
    private val scope = CoroutineScope(SupervisorJob() + webSocketDispatcher)


    private val _matchFlow = MutableSharedFlow<LoadResult<MatchDto, Throwable>>(
        replay = 1, // Always emit the latest counter update
        extraBufferCapacity = 5
    )

    suspend fun addNewMatch(
        tournamentId: String,
        matchBody: MatchBody
    ): LoadResult<ShortMatchDto, Throwable> {
        return runOperationCatching {
            val shortMatchDto = httpClient.post("/tournaments/$tournamentId/matches") {
                setBody(matchBody)
            }.body<ShortMatchDto>()

            println(shortMatchDto)
            shortMatchDto
        }
    }

    suspend fun updateMatchScore(
        matchId: String,
        changeScoreBody: ChangeScoreBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/score") {
                setBody(changeScoreBody)
            }.body<ResponseMessage>()

            println(message)
            message
        }
    }

    suspend fun undoPoint(
        matchId: String,
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/undo").body<ResponseMessage>()

            message
        }
    }

    suspend fun redoPoint(
        matchId: String,
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/redo").body<ResponseMessage>()

            message
        }
    }

    suspend fun setFirstParticipantToServe(
        matchId: String,
        serveBody: ServeBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/firstServe"){
                setBody(serveBody)
            }.body<ResponseMessage>()

            message
        }
    }

    suspend fun setFirstServeInPair(
        matchId: String,
        serveInPairBody: ServeInPairBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/firstServeInPair"){
                setBody(serveInPairBody)
            }.body<ResponseMessage>()

            message
        }
    }

    suspend fun setParticipantRetired(
        matchId: String,
        retiredParticipantBody: RetiredParticipantBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/retire"){
                setBody(retiredParticipantBody)
            }.body<ResponseMessage>()

            message
        }
    }

    suspend fun updateMatchStatus(
        matchId: String,
        matchStatusBody: MatchStatusBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/status") {
                setBody(matchStatusBody)
            }.body<ResponseMessage>()

            message
        }
    }

    suspend fun getMatchesByTournament(
        tournamentId: String,
    ): LoadResult<List<ShortMatchDto>, Throwable> {
        return runOperationCatching {
            val matches =
                httpClient.get("/tournaments/$tournamentId/matches").body<List<ShortMatchDto>>()

            matches
        }
    }

    fun observeMatchUpdates(): SharedFlow<LoadResult<MatchDto, Throwable>> =
        _matchFlow.asSharedFlow() // Expose as read-only flow

    fun connectToMatchUpdates(matchId: String) {
        var reconnectionTime = 5000L

        val appConfig = AppConfig.current
        scope.launch {
            while (true) {
                try {

                    webSocketSession =
                        httpClient.webSocketSession {
                            url {
                                protocol = URLProtocol.WSS
                                host = appConfig.baseUrlBackend
                                port = 443
                                path("/matches/$matchId")
                            }
                        }

                    println("Connected to WebSocket")
                    innerloop@ while (true) { // Внутренний цикл для чтения сообщений
                        try {
                            for (frame in webSocketSession!!.incoming) {
                                when (frame) {
                                    is Frame.Text -> {
                                        println(frame.readText())
                                        val matchDto =
                                            Json.decodeFromString<MatchDto>(frame.readText())
                                        _matchFlow.emit(LoadResult.Success(matchDto))
                                    }

                                    is Frame.Close -> {
                                        println("Connection closed: ${frame.readReason()}")
                                        webSocketSession?.close()
                                        break@innerloop
                                    }

                                    else -> Unit
                                }
                            }
                        } catch (e: Exception) {
                            println("Error reading frame: ${e.message}")
                            _matchFlow.emit(LoadResult.Error(e))
                            reconnectionTime = 1000L
                        }
                    }
                } catch (e: Exception) {
                    _matchFlow.emit(LoadResult.Error(e))
                }
                delay(reconnectionTime)
            }
        }
    }

    suspend fun closeSession() {
        scope.cancel()
        webSocketSession?.close()
    }
}