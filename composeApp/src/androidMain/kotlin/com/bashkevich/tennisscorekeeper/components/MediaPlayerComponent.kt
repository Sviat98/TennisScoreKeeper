package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.bashkevich.tennisscorekeeper.components.expect.LocalFullScreenState
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus

@Composable
fun MediaPlayerComponent(
    modifier: Modifier = Modifier,
    match: Match,
    mediaPlayerHost: MediaPlayerHost,
    onLoadVideoLink: (String)-> Unit
) {
    val videoLinkState = rememberTextFieldState(match.videoLink ?: "")
    val isLiveStream = match.status !in listOf(MatchStatus.PAUSED,MatchStatus.COMPLETED)

    val fullScreenState = LocalFullScreenState.current

    val isFullScreen = fullScreenState.isFullScreen

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFullScreen){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    state = videoLinkState
                )
                Button(onClick = {onLoadVideoLink(videoLinkState.text.toString().trim()) }) {
                    Text("Load")
                }
            }
        }
        val videoPlayerModifier = Modifier.width(300.dp).aspectRatio(16/9f)
        if(match.videoLink == null){
            Box(
               modifier = Modifier.then(videoPlayerModifier).background(color = Color.Black)
            )
        }else{
            VideoPlayerComposable(
                modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.then(videoPlayerModifier) ,
                playerHost = mediaPlayerHost,
                playerConfig = VideoPlayerConfig(
                    seekBarActiveTrackColor = Color.Red,
                    seekBarInactiveTrackColor = Color.White,
                    controlHideIntervalSeconds = 5,
                    enableFullEdgeToEdge = false,
                    isLiveStream = isLiveStream,
                    enableLongPressFastForward = true
                )
            )
        }
    }
}