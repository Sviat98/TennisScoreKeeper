package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import com.bashkevich.tennisscorekeeper.components.expect.LocalFullScreenState
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent

@Composable
fun ScoreboardWithMediaPlayerView(
    modifier: Modifier,
    match: Match,
    mediaPlayerHost: MediaPlayerHost,
    onEvent: (MatchDetailsUiEvent)->Unit
){
    var isMediaPlayerEnabled  by remember { mutableStateOf(match.videoLink != null)  }

    val fullScreenState = LocalFullScreenState.current

    val isFullScreen = fullScreenState.isFullScreen

    Column(
        modifier =Modifier.then(modifier),
        ) {
        if (!isFullScreen){
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Scoreboard")
                Switch(
                    checked = isMediaPlayerEnabled,
                    onCheckedChange = {isMediaPlayerEnabled = !isMediaPlayerEnabled}
                )
                Text("Live Stream")
            }
        }
    }
   if (isMediaPlayerEnabled){
       MediaPlayerComponent(
           match = match,
           mediaPlayerHost = mediaPlayerHost,
           onLoadVideoLink = {videoLink  -> onEvent(MatchDetailsUiEvent.AttachVideoLink(videoLink))}
       )
   }
   else{
       MatchDetailsScoreboardView(
           match = match,
       )
   }

}

//@Composable
//@Preview
//fun MatchDetailsScoreboardWrapperPreview() {
//    MatchDetailsScoreboardWrapper(
//        match = SAMPLE_MATCH
//    ) { }
//}