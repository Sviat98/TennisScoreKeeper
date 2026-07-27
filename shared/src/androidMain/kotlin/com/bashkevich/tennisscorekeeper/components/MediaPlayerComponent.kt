package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.bashkevich.tennisscorekeeper.components.expect.FullScreenState
import com.bashkevich.tennisscorekeeper.components.expect.LocalFullScreenState
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.change_stream_link

@Composable
fun MediaPlayerComponent(
    modifier: Modifier = Modifier,
    match: Match,
    mediaPlayerHost: MediaPlayerHost,
    onLoadVideoLink: (String) -> Unit
) {
    val isLiveStream = match.status !in listOf(MatchStatus.PAUSED, MatchStatus.COMPLETED)

    val fullScreenState = LocalFullScreenState.current

    val isFullScreen = fullScreenState.isFullScreen

    val videoLink = match.videoLink ?: ""

    val videoLinkState = rememberTextFieldState(videoLink)

    LaunchedEffect(videoLink) {
        if (videoLink.isNotBlank()) {
            mediaPlayerHost.loadUrl(videoLink)
        }
    }

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val videoComponentModifier = Modifier.widthIn(max = 300.dp).fillMaxWidth()
        if (!isFullScreen) {
            if (videoLink.isEmpty()) {
                VideoLinkField(
                    modifier = Modifier.then(videoComponentModifier),
                    videoLinkState = videoLinkState,
                    onLoadVideoLink = onLoadVideoLink,
                )
            } else {
                var editorExpanded by remember { mutableStateOf(false) }
                ExpandableSection(
                    modifier = Modifier.then(videoComponentModifier),
                    title = stringResource(Res.string.change_stream_link),
                    expanded = editorExpanded,
                    onToggle = { editorExpanded = !editorExpanded },
                ) {
                    VideoLinkField(
                        videoLinkState = videoLinkState,
                        onLoadVideoLink = onLoadVideoLink,
                    )
                }
            }
        }
        val videoPlayerModifier = Modifier.width(300.dp).aspectRatio(16 / 9f)
        if (videoLink.isEmpty()) {
            Box(
                modifier = Modifier.then(videoPlayerModifier).background(color = Color.Black)
            )
        } else {
            VideoPlayerComposable(
                modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.then(videoPlayerModifier),
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

@Preview
@Composable
fun MediaPlayerComponentEmptyPreview() {
    MaterialTheme {
        CompositionLocalProvider(
            LocalFullScreenState provides FullScreenState(isFullScreen = false) {}
        ) {
            Surface {
                MediaPlayerComponent(
                    match = SAMPLE_MATCH,
                    mediaPlayerHost = remember { MediaPlayerHost() },
                    onLoadVideoLink = {},
                )
            }
        }
    }
}

@Preview
@Composable
fun MediaPlayerComponentWithLinkPreview() {
    MaterialTheme {
        CompositionLocalProvider(
            LocalFullScreenState provides FullScreenState(isFullScreen = false) {}
        ) {
            Surface {
                MediaPlayerComponent(
                    match = SAMPLE_MATCH.copy(
                        videoLink = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                    ),
                    mediaPlayerHost = remember { MediaPlayerHost() },
                    onLoadVideoLink = {},
                )
            }
        }
    }
}
