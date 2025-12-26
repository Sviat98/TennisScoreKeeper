package com.bashkevich.tennisscorekeeper.components.scoreboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet

@Composable
fun CurrentSetComponent(
    modifier: Modifier = Modifier,
    currentSet: TennisSet,
    numberFontSize: TextUnit = 20.sp,
) {
    val mainBackgroundColor = Color(0xFF142c6c)
    Box(modifier = Modifier.then(modifier)) {
        CurrentSetNumbersContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Yellow)
            ,
            currentSet = currentSet,
            numberFontSize = numberFontSize
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
            color = mainBackgroundColor
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            color = mainBackgroundColor
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = mainBackgroundColor
        )
    }

}

@Composable
fun CurrentSetNumbersContainer(
    modifier: Modifier = Modifier,
    currentSet: TennisSet,
    numberFontSize: TextUnit
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
    ) {
        val textColor = Color.Black
        ScoreboardNumber(
            modifier = Modifier.weight(1f),
            scoreNumber = currentSet.firstParticipantGamesWon.toString(),
            textColor = textColor,
            textFontSize = numberFontSize
        )
        ScoreboardNumber(
            modifier = Modifier.weight(1f),
            scoreNumber = currentSet.secondParticipantGamesWon.toString(),
            textColor = textColor,
            textFontSize = numberFontSize
        )
    }
}