package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.scoreboard.ColorScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.match.MatchScoreboardView
import com.bashkevich.tennisscorekeeper.components.match.ShortMatchScoreboardCard
import com.bashkevich.tennisscorekeeper.components.scoreboard.CurrentGamePausedComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.ParticipantColor
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_SINGLES_SHORT_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame


@Composable
@Preview
fun MatchViewPreview() {
    MatchScoreboardView(modifier = Modifier, match = SAMPLE_MATCH)
}

@Composable
@Preview
fun ColorScoreboardComponentPreview() {
    ColorScoreboardComponent(modifier = Modifier.height(32.dp).background(Color.White), match = SAMPLE_MATCH)
}

@Composable
@Preview
fun DoublesMatchViewPreview() {
    MatchScoreboardView(modifier = Modifier, match = DOUBLES_SAMPLE_MATCH)
}

@Composable
@Preview
fun ParticipantColorPreview(){
    ParticipantColor(primaryColor = Color.Red, secondaryColor = null)
}

@Composable
@Preview
fun ShortMatchScoreboardCardPreview(){
    ShortMatchScoreboardCard(
        modifier = Modifier,
        match = SAMPLE_SINGLES_SHORT_MATCH,
        onClick = {}
    )
}
@Composable
@Preview
fun CurrentGamePausedComponentPreview(){
    CurrentGamePausedComponent(
        modifier = Modifier,
        currentGame = TennisGame("40","15"),
    )
}
