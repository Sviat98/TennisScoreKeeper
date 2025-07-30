package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet

@Composable
fun CurrentSetComponent(
    modifier: Modifier = Modifier,
    currentSet: TennisSet
) {
    Box(modifier = Modifier.then(modifier)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Yellow),
            horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
        ) {
            val textColor = Color.Black
            ScoreboardNumber(
                modifier = Modifier.weight(1f),
                scoreNumber = currentSet.firstParticipantGamesWon.toString(),
                textColor = textColor
            )
            ScoreboardNumber(
                modifier = Modifier.weight(1f),
                scoreNumber = currentSet.secondParticipantGamesWon.toString(),
                textColor = textColor
            )
        }
        Divider(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
            color = Color(0xFF142c6c)
        )
        Divider(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            color = Color(0xFF142c6c)
        )
        Divider(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = Color(0xFF142c6c)
        )
    }

}