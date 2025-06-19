package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisGame

@Composable
fun CurrentGameComponent(
    modifier: Modifier = Modifier,
    currentGame: TennisGame
) {
    Box(modifier = Modifier.then(modifier)){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
        ) {
            CurrentGameNumber(
                modifier = Modifier.weight(0.5f),
                pointsEarned = currentGame.firstParticipantPointsWon
            )
            CurrentGameNumber(
                modifier = Modifier.weight(0.5f),
                pointsEarned = currentGame.secondParticipantPointsWon
            )
        }
        Divider(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), color = Color(0xFF142c6c))
        Divider(modifier = Modifier.fillMaxWidth().align(Alignment.Center), color = Color(0xFF142c6c))
        Divider(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter), color = Color(0xFF142c6c))

    }

}

@Composable
fun CurrentGameNumber(
    modifier: Modifier = Modifier,
    pointsEarned: String,
) {
    Box(modifier = Modifier.then(modifier)) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = pointsEarned,
            fontSize = 20.sp,
            color = Color.Black
        )
    }

}