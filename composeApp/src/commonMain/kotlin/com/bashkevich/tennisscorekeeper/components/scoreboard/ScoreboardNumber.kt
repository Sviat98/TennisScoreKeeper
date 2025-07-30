package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun ScoreboardNumber(
    modifier: Modifier = Modifier,
    scoreNumber: String,
    textColor: Color,
) {
    Box(
        modifier = Modifier.then(modifier)
    ){
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = scoreNumber,
            fontSize = 20.sp,
            color =textColor
        )
    }

}