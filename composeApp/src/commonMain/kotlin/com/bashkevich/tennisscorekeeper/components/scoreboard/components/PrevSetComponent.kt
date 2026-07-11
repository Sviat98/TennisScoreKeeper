package com.bashkevich.tennisscorekeeper.components.scoreboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.bashkevich.tennisscorekeeper.model.theme.domain.LocalScoreboardTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet

@Composable
fun PrevSetScoreboardComponent(
    modifier: Modifier = Modifier,
    prevSet: TennisSet,
    numberFontSize: TextUnit = 20.sp,
    isSetFinished: Boolean = true,
    retiredParticipantNumber: Int? = null,
    paddingFromCenter: Dp = 0.dp
) {
    val firstParticipantGamesWon = prevSet.firstParticipantGamesWon
    val secondParticipantGamesWon = prevSet.secondParticipantGamesWon

    val isFirstParticipantWon =
        (retiredParticipantNumber == 2) || (isSetFinished && (firstParticipantGamesWon > secondParticipantGamesWon))
    val isSecondParticipantWon =
        (retiredParticipantNumber == 1) || (isSetFinished && (firstParticipantGamesWon < secondParticipantGamesWon))

    val theme = LocalScoreboardTheme.current
    val winColor = theme.previousSetWinTextColor
    val loseColor = theme.previousSetLoseTextColor
    val colorsAreEqual = winColor == loseColor

    val firstParticipantColor = if (isFirstParticipantWon) winColor else loseColor
    val secondParticipantColor = if (isSecondParticipantWon) winColor else loseColor

    var firstParticipantAlpha = 1f
    var secondParticipantAlpha = 1f

    // может быть ситуация, когда в сете ПОКА нет победителя (если к примеру матч остановили посреди сета)
    if (colorsAreEqual) {
        if (isFirstParticipantWon) {
            secondParticipantAlpha = 0.5f
        }
        if (isSecondParticipantWon) {
            firstParticipantAlpha = 0.5f
        }
    }

    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
    ) {
        ScoreboardNumber(
            modifier = Modifier.weight(1f)
                .padding(bottom = paddingFromCenter),
            scoreNumber = prevSet.firstParticipantGamesWon.toString(),
            textColor = firstParticipantColor.copy(alpha = firstParticipantAlpha),
            textFontSize = numberFontSize
        )
        ScoreboardNumber(
            modifier = Modifier.weight(1f).padding(top = paddingFromCenter),
            scoreNumber = prevSet.secondParticipantGamesWon.toString(),
            textColor = secondParticipantColor.copy(alpha = secondParticipantAlpha),
            textFontSize = numberFontSize
        )
    }
}

