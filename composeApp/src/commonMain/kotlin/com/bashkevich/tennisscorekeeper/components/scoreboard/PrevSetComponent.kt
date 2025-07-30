package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet

@Composable
fun PrevSetScoreboardComponent(
    modifier: Modifier = Modifier,
    prevSet: TennisSet,
    showWithoutHighlighting: Boolean,
) {
    val firstParticipantGamesWon = prevSet.firstParticipantGamesWon
    val secondParticipantGamesWon = prevSet.secondParticipantGamesWon

    val isFirstParticipantWon = firstParticipantGamesWon > secondParticipantGamesWon

    var firstParticipantAlpha: Float
    var secondParticipantAlpha: Float

    if (showWithoutHighlighting){
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

    val textColor = Color.White

    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
    ) {
        ScoreboardNumber(
            modifier = Modifier.weight(1f),
            scoreNumber = prevSet.firstParticipantGamesWon.toString(),
            textColor = textColor.copy(alpha = firstParticipantAlpha)
        )
        ScoreboardNumber(
            modifier = Modifier.weight(1f),
            scoreNumber = prevSet.secondParticipantGamesWon.toString(),
            textColor = textColor.copy(alpha = secondParticipantAlpha)
        )
    }
}

