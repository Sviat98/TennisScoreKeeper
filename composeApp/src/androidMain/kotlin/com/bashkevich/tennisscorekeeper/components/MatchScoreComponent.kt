package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ChooseServePanel
import com.bashkevich.tennisscorekeeper.components.scoreboard.overlay.MatchScoreboardView
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH


@Composable
@Preview
fun MatchViewPreview() {
    MatchScoreboardView(modifier = Modifier, match = SAMPLE_MATCH)
}

@Composable
@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160")
fun ChooseServePanelPreview() {
    ChooseServePanel(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .background(Color.White),
        match = DOUBLES_SAMPLE_MATCH,
        onFirstParticipantToServeChoose = {  } ,
        onFirstPlayerInPairToServeChoose = {},
    )
}

//@Composable
//@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160")
//fun ChooseServePanelPreview() {
//    ChooseServePanel(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White),
//        match = DOUBLES_SAMPLE_MATCH,
//        onFirstParticipantToServeChoose = {  } ,
//        onFirstPlayerInPairToServeChoose = {},
//    )
//}