package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet

@Composable
fun PrevSetScoreboardComponent(
    modifier: Modifier = Modifier,
    prevSet: TennisSet
) {
    val firstPlayerGamesWon = prevSet.firstParticipantGamesWon
    val secondPlayerGamesWon = prevSet.secondParticipantGamesWon

    val isFirstPlayerWon = firstPlayerGamesWon > secondPlayerGamesWon
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
    ) {
        PrevSetNumber(
            modifier = Modifier.weight(0.5f),
            gamesEarned = prevSet.firstParticipantGamesWon,
            alpha = if (isFirstPlayerWon) 1f else 0.5f
        )
        PrevSetNumber(
            modifier = Modifier.weight(0.5f),
            gamesEarned = prevSet.secondParticipantGamesWon,
            alpha = if (!isFirstPlayerWon) 1f else 0.5f
        )
    }
}

@Composable
fun PrevSetNumber(
    modifier: Modifier = Modifier,
    gamesEarned: Int,
    alpha: Float,
) {
    Box(
        modifier = Modifier.then(modifier)
    ){
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = gamesEarned.toString(),
            fontSize = 20.sp,
            color = Color.White.copy(alpha = alpha)
        )
    }

}