package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import chaintech.videoplayer.util.isMobile
import com.bashkevich.tennisscorekeeper.components.expect.LocalFullScreenState
import com.bashkevich.tennisscorekeeper.components.expect.LocalMediaPlayerHost
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun MediaPlayerComponent(
    modifier: Modifier = Modifier,
    match: Match
) {
    val videoLinkState = rememberTextFieldState(match.videoLink ?: "")
    val isLiveStream = match.status !in listOf(MatchStatus.PAUSED,MatchStatus.COMPLETED)

    val fullScreenState = LocalFullScreenState.current

    val isFullScreen = fullScreenState.isFullScreen

    val mediaPlayerHost = LocalMediaPlayerHost.current

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFullScreen){
            TextField(
                state = videoLinkState
            )
        }
        VideoPlayerComposable(
            modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.width(300.dp).height(250.dp),
            playerHost = mediaPlayerHost,
            playerConfig = VideoPlayerConfig(
                seekBarActiveTrackColor = Color.Red,
                seekBarInactiveTrackColor = Color.White,
                seekBarBottomPadding = 8.sdp,
                pauseResumeIconSize = if (!isMobile()) 18.sdp else 30.sdp,
                controlHideIntervalSeconds = 5,
                topControlSize = if (!isMobile()) 16.sdp else 20.sdp,
                fastForwardBackwardIconSize = if (!isMobile()) 16.sdp else 28.sdp,
                controlTopPadding = 10.sdp,
                enableFullEdgeToEdge = false,
                isLiveStream = isLiveStream,
                enableLongPressFastForward = true
            )
        )
    }
}