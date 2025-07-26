package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeedScoreboardComponent(
    modifier: Modifier = Modifier,
    firstParticipantSeed: Int?,
    secondParticipantSeed: Int?,
) {

    var maxSeedWidth by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.then(modifier).padding(start = if (maxSeedWidth > 0) 4.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SeedNumber(
            modifier = Modifier.weight(1f).onGloballyPositioned { layoutCoordinates ->

                val currentWidth = layoutCoordinates.size.width

                if (currentWidth > maxSeedWidth) {
                    maxSeedWidth = currentWidth
                }
            },
            seedNumber = firstParticipantSeed ?: 0
        )
        SeedNumber(
            modifier = Modifier.weight(1f).onGloballyPositioned { layoutCoordinates ->

                val currentWidth = layoutCoordinates.size.width

                if (currentWidth > maxSeedWidth) {
                    maxSeedWidth = currentWidth
                }
            },
            seedNumber = secondParticipantSeed ?: 0
        )
    }

}

@Composable
fun SeedNumber(
    modifier: Modifier = Modifier,
    seedNumber: Int
) {
    Box(modifier = Modifier.then(modifier)) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = if (seedNumber == 0) "" else seedNumber.toString(),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }

}