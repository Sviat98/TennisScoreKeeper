package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.bashkevich.tennisscorekeeper.components.expect.LocalFullScreenState
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.SmartDisplay
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.change_stream_link
import tennisscorekeeper.shared.generated.resources.load_video

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
        if (!isFullScreen) {
            if (videoLink.isEmpty()) {
                VideoLinkField(
                    videoLinkState = videoLinkState,
                    onLoadVideoLink = onLoadVideoLink,
                )
            } else {
                var editorExpanded by remember { mutableStateOf(false) }
                ExpandableSection(
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

@Composable
private fun VideoLinkField(
    videoLinkState: TextFieldState,
    onLoadVideoLink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            state = videoLinkState,
            modifier = Modifier.weight(1f),
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        IconButton(onClick = { onLoadVideoLink(videoLinkState.text.toString().trim()) }) {
            Icon(
                imageVector = IconGroup.Default.SmartDisplay,
                contentDescription = stringResource(Res.string.load_video),
            )
        }
    }
}
