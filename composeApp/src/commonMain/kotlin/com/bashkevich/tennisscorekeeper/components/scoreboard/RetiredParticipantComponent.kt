package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun RetiredParticipantComponent(
    modifier: Modifier = Modifier,
    match: Match
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val isFirstParticipantRetired = firstParticipant.isRetired
    val isSecondParticipantRetired = secondParticipant.isRetired


    val hasParticipantRetired = isFirstParticipantRetired || isSecondParticipantRetired

    if (hasParticipantRetired) {
        Column(
            modifier = Modifier.then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isFirstParticipantRetired) {
                RetiredSign(
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
                RetiredSign(
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun RetiredSign(modifier: Modifier = Modifier){
    Box(modifier = Modifier.then(modifier)) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Ret.",
            fontSize = 20.sp,
            color = Color.White
        )
    }
}