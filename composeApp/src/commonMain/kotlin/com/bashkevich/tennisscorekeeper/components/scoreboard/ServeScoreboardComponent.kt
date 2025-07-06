package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun ServeScoreboardComponent(
    modifier: Modifier = Modifier,
    match: Match
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val isWinnerInMatch = firstParticipant.isWinner || secondParticipant.isWinner

    if (!isWinnerInMatch) {
        Column(
            modifier = Modifier.then(modifier)
        ) {
            ServingBox(modifier = Modifier.weight(1f), showServe = firstParticipant.isServing)
            ServingBox(modifier = Modifier.weight(1f), showServe = secondParticipant.isServing)
        }
    }


}

@Composable
fun ServingBox(
    modifier: Modifier = Modifier,
    showServe: Boolean
){
    Box(modifier = Modifier.then(modifier)){
        if (showServe){
            Box(
                modifier = Modifier.size(8.dp).clip(
                    CircleShape
                ).background(color = Color.Yellow).align(Alignment.Center)
            )
        }
    }
}