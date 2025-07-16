package com.bashkevich.tennisscorekeeper.components.scoreboard

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
    prevSet: TennisSet,
    markSetAsRetired: Boolean,
) {
    val firstParticipantGamesWon = prevSet.firstParticipantGamesWon
    val secondParticipantGamesWon = prevSet.secondParticipantGamesWon

    val isFirstParticipantWon = firstParticipantGamesWon > secondParticipantGamesWon

    var firstParticipantAlpha: Float
    var secondParticipantAlpha: Float

    if (markSetAsRetired){
        firstParticipantAlpha = 1f
        secondParticipantAlpha = 1f
    }else{
        if (isFirstParticipantWon){
            firstParticipantAlpha = 1f
            secondParticipantAlpha = 0.5f
        }else{
            firstParticipantAlpha = 0.5f
            secondParticipantAlpha = 1f
        }
    }

    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
    ) {
        PrevSetNumber(
            modifier = Modifier.weight(1f),
            gamesEarned = prevSet.firstParticipantGamesWon,
            alpha = firstParticipantAlpha
        )
        PrevSetNumber(
            modifier = Modifier.weight(1f),
            gamesEarned = prevSet.secondParticipantGamesWon,
            alpha = secondParticipantAlpha
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