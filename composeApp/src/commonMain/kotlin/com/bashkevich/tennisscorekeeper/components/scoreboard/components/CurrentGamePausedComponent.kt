package com.bashkevich.tennisscorekeeper.components.scoreboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame

@Composable
fun CurrentGamePausedComponent(
    modifier: Modifier = Modifier,
    currentGame: TennisGame
) {
    Box(
        modifier = Modifier.then(modifier)
    ) {
        Canvas(modifier = Modifier.matchParentSize().padding(2.dp)) {
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = size.toRect(),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                )
            }
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp).align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
        ) {
            val textColor = Color.White
            ScoreboardNumber(
                modifier = Modifier.weight(1f),
                scoreNumber = currentGame.firstParticipantPointsWon,
                textColor = textColor,
                textFontSize = 16.sp
            )
            ScoreboardNumber(
                modifier = Modifier.weight(1f),
                scoreNumber = currentGame.secondParticipantPointsWon,
                textColor = textColor,
                textFontSize = 16.sp
            )
        }
    }


}