package com.bashkevich.tennisscorekeeper.components.scoreboard.match_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.CurrentSetNumbersContainer
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisSet

@Composable
fun MatchDetailsCurrentSetComponent(
    modifier: Modifier = Modifier,
    currentSet: TennisSet,
    numberFontSize: TextUnit = 20.sp,
) {
    val mainBackgroundColor = Color(0xFF142c6c)
    Box(modifier = Modifier.then(modifier)) {
        CurrentSetNumbersContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Yellow),
                //.padding(horizontal = 4.dp),
            currentSet = currentSet,
            numberFontSize = numberFontSize
        )
        Divider(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
            color = mainBackgroundColor
        )
        Divider(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            color = mainBackgroundColor
        )
        Divider(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            color = mainBackgroundColor
        )
    }
}