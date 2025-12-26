package com.bashkevich.tennisscorekeeper.components.expect

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.MatchDetailsAppBar
import com.bashkevich.tennisscorekeeper.components.match_details.MatchStatusButton
import com.bashkevich.tennisscorekeeper.components.match_details.ParticipantsPointsControlPanel
import com.bashkevich.tennisscorekeeper.components.match_details.RetireParticipantPanel
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ChooseServePanel
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.convertToString
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.ProfileRoute
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent
import kotlinx.coroutines.launch

// Класс состояния с значением и функцией обновления
class FullScreenState(
    val isFullScreen: Boolean,
    val setFullScreenValue: (Boolean) -> Unit
)
//
val LocalFullScreenState = compositionLocalOf<FullScreenState> {
    error("FullScreenState не предоставлен")
}

val LocalMediaPlayerHost = compositionLocalOf<MediaPlayerHost> {
    error("MediaPlayerHost не предоставлен")
}

@Composable
actual fun MatchDetailsContentWrapper(
    modifier: Modifier,
    state: MatchDetailsState,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {
    val navController = LocalNavHostController.current
    val match = state.match

    val clipboard = LocalClipboard.current

    val isAuthorized = LocalAuthorization.current

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

//    // Создаем состояние
    var isFullScreen by remember { mutableStateOf(false) }
//
//    // Создаем экземпляр FullScreenState
    val fullScreenState = remember(isFullScreen) {
        FullScreenState(
            isFullScreen = isFullScreen,
            setFullScreenValue = { newValue->
                isFullScreen = newValue
            }
        )
    }

    val videoLink = match.videoLink ?: "https://www.youtube.com/watch?v=q0jH2gyFqAQ"
    val mediaPlayerHost = remember { MediaPlayerHost(mediaUrl = videoLink) }

    mediaPlayerHost.onEvent = { event->
        when(event){
            is MediaPlayerEvent.FullScreenChange->{
                fullScreenState.setFullScreenValue(!isFullScreen)
            }
            else -> {}
        }
    }

    // Обработка кнопки "назад"
    DisposableEffect(Unit) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullScreen) {
                    mediaPlayerHost.setFullScreen(false)
                    fullScreenState.setFullScreenValue(false)
                }else{
                    navController.navigateUp()
                }
            }
        }
        backPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }

    val scope = rememberCoroutineScope()
    CompositionLocalProvider(
        LocalFullScreenState provides fullScreenState,
        LocalMediaPlayerHost provides mediaPlayerHost
    ) {
        Scaffold(
            modifier = Modifier.then(modifier),
            topBar = {
                if (!isFullScreen) {
                    MatchDetailsAppBar(
                        matchId = match.id,
                        onBack = { navController.navigateUp() },
                        onCopyLink = { link ->
                            scope.launch {
                                clipboard.setText(link)
                            }
                        },
                        isAuthorized = isAuthorized,
                        onNavigateToLoginOrProfile = {
                            if (isAuthorized) {
                                navController.navigate(ProfileRoute)
                            } else {
                                navController.navigate(LoginRoute)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            val boxModifier =
                if (isFullScreen) Modifier.fillMaxSize().padding(paddingValues) else Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(all = 16.dp)
                    .verticalScroll(state = rememberScrollState())
            Box(
                modifier = Modifier.then(boxModifier)
            ) {
                val columnModifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                Column(
                    modifier = Modifier.then(columnModifier),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MatchDetailsScoreboardWrapper(
                        modifier = Modifier.horizontalScroll(state = rememberScrollState()),
                        match = match,
                        onEvent = onEvent
                    )
                    if (!isFullScreen) { // показываем, ТОЛЬКО если видео не на полный экран
                        Text("Status: ${match.status.convertToString()}")

                        MatchStatusButton(
                            match = match,
                            onStatusChange = { status ->
                                onEvent(
                                    MatchDetailsUiEvent.ChangeMatchStatus(
                                        status = status
                                    )
                                )
                            }
                        )

                        when (match.status) {
                            MatchStatus.NOT_STARTED -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                ChooseServePanel(
                                    modifier = Modifier.fillMaxWidth(),
                                    match = match,
                                    onFirstParticipantToServeChoose = { participantId ->
                                        onEvent(
                                            MatchDetailsUiEvent.SetFirstParticipantToServe(
                                                participantId = participantId
                                            )
                                        )
                                    },
                                    onFirstPlayerInPairToServeChoose = { playerId ->
                                        onEvent(
                                            MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                                playerId = playerId
                                            )
                                        )
                                    }
                                )
                            }

                            MatchStatus.IN_PROGRESS -> {
                                ParticipantsPointsControlPanel(
                                    modifier = Modifier.fillMaxWidth(),
                                    match = match,
                                    onUpdateScore = { participantId, scoreType ->
                                        onEvent(
                                            MatchDetailsUiEvent.UpdateScore(
                                                participantId = participantId,
                                                scoreType = scoreType
                                            )
                                        )
                                    },
                                    onUndoPoint = { onEvent(MatchDetailsUiEvent.UndoPoint) },
                                    onRedoPoint = { onEvent(MatchDetailsUiEvent.RedoPoint) }
                                )
                            }

                            MatchStatus.PAUSED -> {
                                RetireParticipantPanel(
                                    modifier = Modifier.fillMaxWidth(),
                                    match = match,
                                    onParticipantRetire = { participantId ->
                                        onEvent(
                                            MatchDetailsUiEvent.SetParticipantRetired(
                                                participantId = participantId
                                            )
                                        )
                                    }
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//actual fun MatchDetailsContentWrapper(
//    modifier: Modifier,
//    state: MatchDetailsState,
//    onEvent: (MatchDetailsUiEvent) -> Unit
//) {
//    val navController = LocalNavHostController.current
//    val match = state.match
//
//    val clipboard = LocalClipboard.current
//
//    val isAuthorized = LocalAuthorization.current
//
//    // Создаем состояние
//    var isFullScreenEnabled by remember { mutableStateOf(false) }
//
//    // Создаем экземпляр FullScreenState
//    val fullScreenState = remember(isFullScreenEnabled) {
//        FullScreenState(
//            isFullScreenEnabled = isFullScreenEnabled,
//            toggleValue = {
//                !isFullScreenEnabled
//            }
//        )
//    }
//
//    val scope = rememberCoroutineScope()
//
//    val mediaUrl = "https://www.youtube.com/watch?v=EVHs7jmRdXk"
//    val mediaPlayerHost = remember { MediaPlayerHost(mediaUrl = mediaUrl) }
//
//    mediaPlayerHost.onEvent = { event ->
//        when (event) {
//            is MediaPlayerEvent.FullScreenChange -> {
//                isFullScreenEnabled = !isFullScreenEnabled
//            }
//
//            else -> {}
//        }
//    }
//    CompositionLocalProvider(
//        LocalFullScreenState provides fullScreenState
//    ) {
//        Scaffold(
//            modifier = Modifier.then(modifier),
//            topBar = {
//                if (!isFullScreenEnabled) {
//                    MatchDetailsAppBar(
//                        matchId = match.id,
//                        onBack = { navController.navigateUp() },
//                        onCopyLink = { link ->
//                            scope.launch {
//                                clipboard.setText(link)
//                            }
//                        },
//                        isAuthorized = isAuthorized,
//                        onNavigateToLoginOrProfile = {
//                            if (isAuthorized) {
//                                navController.navigate(ProfileRoute)
//                            } else {
//                                navController.navigate(LoginRoute)
//                            }
//                        }
//                    )
//                }
//            }
//        ) { paddingValues ->
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//                    .padding(all = 16.dp)
//                    .background(Color.Yellow)
//                    .verticalScroll(state = rememberScrollState())
//            ) {
//                Column(
//                    modifier = Modifier
//
//                        .align(Alignment.Center)
//                        .background(Color.Green),
//                    verticalArrangement = Arrangement.SpaceAround,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    VideoPlayerComposable(
//                        modifier = if (isFullScreenEnabled) Modifier.fillMaxSize() else Modifier.width(
//                            300.dp
//                        ).aspectRatio(16 / 9f)
//                            .background(Color.Blue),
//                        playerHost = mediaPlayerHost,
//                        playerConfig = VideoPlayerConfig(
//                            enableFullEdgeToEdge = false
//                        )
//                    )
//                }
//            }
//        }
//    }
//}