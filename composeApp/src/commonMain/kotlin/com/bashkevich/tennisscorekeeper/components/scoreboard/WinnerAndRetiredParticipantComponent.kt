package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun WinnerAndRetiredParticipantComponent(
    modifier: Modifier = Modifier,
    match: Match
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val isFirstParticipantRetired = firstParticipant.isRetired
    val isSecondParticipantRetired = secondParticipant.isRetired

    val isFirstParticipantWon = firstParticipant.isWinner
    val isSecondParticipantWon = secondParticipant.isWinner


    val hasParticipantWon = isFirstParticipantWon || isSecondParticipantWon

    if (hasParticipantWon) {
        Column(
            modifier = Modifier.then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isFirstParticipantWon) {
                WinnerSign(
                    modifier = Modifier.weight(1f),
                    contentDescription = "First participant won"
                )
                if (isSecondParticipantRetired) {
                    RetiredSign(
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                if (isFirstParticipantRetired) {
                    RetiredSign(
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                WinnerSign(
                    modifier = Modifier.weight(1f),
                    contentDescription = "Second participant won"
                )
            }
        }
    }
}

@Composable
fun RetiredSign(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.then(modifier)) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Ret.",
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

@Composable
fun WinnerSign(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(modifier = Modifier.then(modifier)) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Default.Check,
            tint = Color.White,
            contentDescription = contentDescription
        )
    }

}