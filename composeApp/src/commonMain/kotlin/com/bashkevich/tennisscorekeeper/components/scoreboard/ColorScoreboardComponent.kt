package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun ColorScoreboardComponent(
    modifier: Modifier = Modifier,
    match: Match
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ParticipantColor(
            modifier = Modifier.weight(1f).width(8.dp),
            primaryColor = firstParticipant.primaryColor,
            secondaryColor = firstParticipant.secondaryColor
        )
        ParticipantColor(
            modifier = Modifier.weight(1f).width(8.dp),
            primaryColor = secondParticipant.primaryColor,
            secondaryColor = secondParticipant.secondaryColor
        )
    }
}

@Composable
fun ParticipantColor(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    secondaryColor: Color?
) {

    Column(modifier = Modifier.then(modifier)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(primaryColor))
        secondaryColor?.let {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(secondaryColor))
        }
    }
}
