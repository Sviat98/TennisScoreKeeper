package com.bashkevich.tennisscorekeeper.model.match.remote

import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.ResponseMessage
import com.bashkevich.tennisscorekeeper.core.remote.runOperationCatching
import com.bashkevich.tennisscorekeeper.core.remote.webSocketDispatcher
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ChangeScoreBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatusBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.RetiredParticipantBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ServeInPairBody
import com.bashkevich.tennisscorekeeper.model.match.remote.body.VideoLinkBody
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
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
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class MatchRemoteDataSource(
    private val httpClient: HttpClient
) {
    private var webSocketSession: DefaultClientWebSocketSession? = null
    private val scope = CoroutineScope(SupervisorJob() + webSocketDispatcher)
    private var connectionJob: Job? = null

    private val _connectionStateFlow = MutableStateFlow(ConnectionState.Loading)

    private val _matchFlow = MutableSharedFlow<LoadResult<MatchDto, Throwable>>(
        replay = 1,
        extraBufferCapacity = 5
    )

    private val wsJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()

    fun observeConnectionState(): StateFlow<ConnectionState> =
        _connectionStateFlow.asStateFlow()

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

    suspend fun attachVideoLink(
        matchId: String,
        videoLinkBody: VideoLinkBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/video") {
                setBody(videoLinkBody)
            }.body<ResponseMessage>()

            message
        }
    }

    suspend fun setFirstParticipantToServe(
        matchId: String,
        serveBody: ServeBody
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/matches/$matchId/firstServe") {
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
            val message = httpClient.patch("/matches/$matchId/firstServeInPair") {
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
            val message = httpClient.patch("/matches/$matchId/retire") {
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

    @OptIn(ExperimentalAtomicApi::class)
    fun connectToMatchUpdates(matchId: String) {
        val appConfig = AppConfig.current

        connectionJob?.cancel()
        connectionJob = scope.launch {
            var reconnectAttempt = 0

            // Loading — только на самое первое подключение;
            // при повторных попытках остаётся Disconnected, чтобы не мигать полноэкранным спиннером
            _connectionStateFlow.value = ConnectionState.Loading

            while (isActive) {
                var session: DefaultClientWebSocketSession? = null

                try {
                    session = httpClient.webSocketSession {
                        url {
                            protocol = URLProtocol.WSS
                            host = appConfig.baseHostBackend
                            port = 443
                            path("/matches/$matchId")
                        }
                    }
                    val activeSession = session
                    webSocketSession = activeSession

                    println("Connected to WebSocket")
                    _connectionStateFlow.value = ConnectionState.Connected
                    reconnectAttempt = 0

                    // Время последнего сообщения сервера — своё на каждое соединение.
                    // Любое сообщение (MatchDto или ответ на heartbeat) означает, что соединение живо
                    val lastServerMessageMs = AtomicLong(nowMs())

                    coroutineScope {
                        val readerJob = launch {
                            for (frame in activeSession.incoming) {
                                when (frame) {
                                    is Frame.Text -> {
                                        val text = frame.readText()
                                        lastServerMessageMs.store(nowMs())

                                        val matchDto = try {
                                            wsJson.decodeFromString<MatchDto>(text)
                                        } catch (e: Exception) {
                                            // Неотдекодируемый фрейм (например, heartbeat_ack) не рвёт соединение
                                            println("Skipping non-MatchDto frame: $text")
                                            continue
                                        }

                                        _matchFlow.emit(LoadResult.Success(matchDto))
                                    }

                                    is Frame.Close -> println("Connection closed: ${frame.readReason()}")

                                    else -> Unit // Ping/Pong/Binary — control frames, их обрабатывает движок
                                }
                            }

                            // incoming закрылся без exception — сервер закрыл соединение
                            throw CancellationException("WebSocket incoming channel closed")
                        }

                        // Application-level heartbeat — источник истины для ConnectionState
                        // на всех платформах (protocol-level ping/pong остаётся делом движка)
                        launch {
                            while (isActive) {
                                delay(HEARTBEAT_INTERVAL_MS.milliseconds)

                                val timeSinceLastServerMessageMs = nowMs() - lastServerMessageMs.load()

                                // Если сервер недавно что-то присылал, heartbeat не нужен
                                if (timeSinceLastServerMessageMs < HEARTBEAT_INTERVAL_MS) {
                                    continue
                                }

                                println("Sending application heartbeat")
                                val heartbeatSentAtMs = nowMs()

                                activeSession.send(Frame.Text(HEARTBEAT))

                                delay(HEARTBEAT_TIMEOUT_MS.milliseconds)

                                if (lastServerMessageMs.load() < heartbeatSentAtMs) {
                                    println("Heartbeat timeout, closing connection")
                                    readerJob.cancel() // выводит reader из зависшего incoming (важно для wasmJs)
                                    activeSession.close(
                                        CloseReason(CloseReason.Codes.GOING_AWAY, "Heartbeat timeout")
                                    )
                                    throw CancellationException("Heartbeat timeout")
                                }
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    // connectionJob отменён извне (closeSession) — выходим из цикла полностью
                    if (!isActive) break

                    println("WebSocket connection cancelled: ${e.message}")
                } catch (e: Exception) {
                    println("WebSocket connection error: ${e.message}")
                    _connectionStateFlow.value = ConnectionState.Disconnected
                    _matchFlow.emit(LoadResult.Error(e))
                } finally {
                    runCatching { session?.close() }
                    if (webSocketSession === session) {
                        webSocketSession = null
                    }
                    if (isActive) {
                        _connectionStateFlow.value = ConnectionState.Disconnected
                    }
                }

                if (isActive) {
                    val reconnectionDelayMs = minOf(
                        RECONNECTION_BASE_DELAY_MS shl reconnectAttempt,
                        RECONNECTION_MAX_DELAY_MS
                    )
                    println("Reconnecting in $reconnectionDelayMs ms...")
                    delay(reconnectionDelayMs.milliseconds)
                    reconnectAttempt++
                }
            }
        }
    }

    // Легаси-реализация без application-heartbeat и бэкоффа, оставлена как запасная
    fun connectToMatchUpdatesLegacy(matchId: String) {
        val reconnectionTime = 5000L

        val appConfig = AppConfig.current
        connectionJob?.cancel()
        connectionJob = scope.launch {
            _connectionStateFlow.value = ConnectionState.Loading
            while (true) {
                try {
                    webSocketSession =
                        httpClient.webSocketSession {
                            url {
                                protocol = URLProtocol.WSS
                                host = appConfig.baseHostBackend
                                port = 443
                                path("/matches/$matchId")
                            }
                        }

                    println("Connected to WebSocket")
                    _connectionStateFlow.value = ConnectionState.Connected

                    innerLoop@ while (true) { // Внутренний цикл для чтения сообщений
                        try {
                            for (frame in webSocketSession!!.incoming) {
                                println("frame from innerLoop = $frame")
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
                                        break@innerLoop
                                    }

                                    else -> Unit
                                }
                            }
                        } catch (e: Exception) {
                            println("Error reading frame: ${e.message}")
                            _matchFlow.emit(LoadResult.Error(e))
                            _connectionStateFlow.value = ConnectionState.Disconnected
                            break@innerLoop
                        }
                    }
                } catch (e: Exception) {
                    _matchFlow.emit(LoadResult.Error(e))
                    _connectionStateFlow.value = ConnectionState.Disconnected
                }
                delay(reconnectionTime.milliseconds)
            }
        }
    }

    fun closeSession() {
        _connectionStateFlow.value = ConnectionState.Disconnected
        connectionJob?.cancel()
        scope.launch { webSocketSession?.close() }
    }

    private companion object {
        const val HEARTBEAT_INTERVAL_MS = 15_000L
        const val HEARTBEAT_TIMEOUT_MS = 10_000L
        const val RECONNECTION_BASE_DELAY_MS = 5_000L
        const val RECONNECTION_MAX_DELAY_MS = 60_000L
        const val HEARTBEAT = """{"type":"heartbeat"}"""
    }
}