package com.bashkevich.tennisscorekeeper.components.scoreboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame

@Composable
fun CurrentGameInProgressComponent(
    modifier: Modifier = Modifier,
    currentGame: TennisGame,
    fontSize: TextUnit = 20.sp
) {
    Box(modifier = Modifier.then(modifier)){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
        ) {
            val textColor = Color.Black
            ScoreboardNumber(
                modifier = Modifier.weight(1f),
                scoreNumber = currentGame.firstParticipantPointsWon,
                textColor = textColor,
                textFontSize = fontSize
            )
            ScoreboardNumber(
                modifier = Modifier.weight(1f),
                scoreNumber = currentGame.secondParticipantPointsWon,
                textColor = textColor,
                textFontSize = fontSize
            )
        }
        Divider(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), color = Color(0xFF142c6c))
        Divider(modifier = Modifier.fillMaxWidth().align(Alignment.Center), color = Color(0xFF142c6c))
        Divider(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter), color = Color(0xFF142c6c))
    }

}