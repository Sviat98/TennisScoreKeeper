package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.components.add_match.AddMatchComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ColorScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.overlay.MatchScoreboardView
import com.bashkevich.tennisscorekeeper.components.scoreboard.short.ShortMatchScoreboardCard
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.CurrentSetComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ParticipantColor
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.WinnerAndRetiredParticipantComponent
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_DOUBLES_SHORT_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_SINGLES_SHORT_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchState


@Composable
@Preview
fun MatchViewPreview() {
    MatchScoreboardView(modifier = Modifier, match = SAMPLE_MATCH)
}

@Composable
@Preview
fun ColorScoreboardComponentPreview() {
    ColorScoreboardComponent(
        modifier = Modifier
            .height(32.dp)
            .background(Color.White),
        match = SAMPLE_MATCH
    )
}

@Composable
@Preview
fun DoublesMatchViewPreview() {
    MatchScoreboardView(modifier = Modifier, match = DOUBLES_SAMPLE_MATCH)
}

@Composable
@Preview
fun ParticipantColorPreview() {
    ParticipantColor(primaryColor = Color.Red, secondaryColor = null)
}

@Composable
@Preview
fun ShortMatchScoreboardCardPreview() {
    ShortMatchScoreboardCard(
        modifier = Modifier,
        match = SAMPLE_SINGLES_SHORT_MATCH,
        onClick = {}
    )
}

@Composable
@Preview
fun ShortDoublesMatchScoreboardCardPreview() {
    MaterialTheme {
        ShortMatchScoreboardCard(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth()
                .hoverScaleEffect(),
            match = SAMPLE_DOUBLES_SHORT_MATCH,
            onClick = {}
        )
    }
}

@Composable
@Preview
fun CurrentGamePausedComponentPreview() {
    CurrentSetComponent(
        modifier = Modifier.height(64.dp).background(color = Color.Yellow).padding(horizontal = 4.dp)
            .padding(horizontal = 1.dp),
        currentSet = TennisSet(5,1),
        numberFontSize = 16.sp
    )
}

@Composable
@Preview(device = "spec:width=3000dp,height=1080dp,dpi=160")
fun AddMatchComponentPreview() {
    AddMatchComponent(
        modifier = Modifier.background(Color.White),
        state = AddMatchState.initial(),
        onEvent = {}) { }
}

@Composable
@Preview(device = "spec:width=411dp,height=891dp", showBackground = false)
fun AddMatchComponentPreviewPhone() {
    AddMatchComponent(
        modifier = Modifier.background(Color.White).width(1000.dp).height(700.dp),
        state = AddMatchState.initial(),
        onEvent = {}) { }
}

@Composable
@Preview
fun WinnerAndRetiredParticipantComponentPhone() {
    WinnerAndRetiredParticipantComponent(
        modifier = Modifier.background(Color.Blue).width(32.dp).height(64.dp),
        firstParticipantId = "1",
        secondParticipantId = "2",
        winnerParticipantId = "1",
        retiredParticipantId = "2",
        paddingFromCenter = 4.dp,
    )
}
