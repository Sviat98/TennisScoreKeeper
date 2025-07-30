package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame

@Composable
fun CurrentGamePausedComponent(
    modifier: Modifier = Modifier,
    currentGame: TennisGame
) {
    Column(
        modifier = Modifier.then(modifier).padding(2.dp)
            .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
    ) {
        val textColor = Color.White
        ScoreboardNumber(
            modifier = Modifier.weight(1f),
            scoreNumber = currentGame.firstParticipantPointsWon,
            textColor = textColor
        )
        ScoreboardNumber(
            modifier = Modifier.weight(1f),
            scoreNumber = currentGame.secondParticipantPointsWon,
            textColor = textColor
        )
    }
}