package com.bashkevich.tennisscorekeeper.components.scoreboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WinnerAndRetiredParticipantComponent(
    modifier: Modifier = Modifier,
    firstParticipantId: String,
    secondParticipantId: String,
    winnerParticipantId: String?,
    retiredParticipantId: String?,
    paddingFromCenter: Dp = 0.dp,
    winnerIconSize: Dp = 24.dp,
    retiredLabelFontSize: TextUnit = 20.sp
) {
    val hasParticipantWon = winnerParticipantId != null

    if (hasParticipantWon) {
        Column(
            modifier = Modifier.then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val winnerParticipantNumber = if (winnerParticipantId == firstParticipantId) 1 else 2

            val retiredParticipantNumber = when (retiredParticipantId) {
                firstParticipantId -> 1
                secondParticipantId -> 2
                else -> null
            }

            WinnerRetiredContainer(
                modifier = Modifier.weight(1f).padding(bottom = paddingFromCenter),
                participantNumber = 1,
                winnerParticipantNumber = winnerParticipantNumber,
                retiredParticipantNumber = retiredParticipantNumber,
                winnerIconSize = winnerIconSize,
                retiredLabelFontSize = retiredLabelFontSize
            )
            WinnerRetiredContainer(
                modifier = Modifier.weight(1f).padding(top = paddingFromCenter),
                participantNumber = 2,
                winnerParticipantNumber = winnerParticipantNumber,
                retiredParticipantNumber = retiredParticipantNumber,
                winnerIconSize = winnerIconSize,
                retiredLabelFontSize = retiredLabelFontSize
            )
        }
    }
}

@Composable
fun WinnerRetiredContainer(
    modifier: Modifier = Modifier,
    participantNumber: Int,
    winnerParticipantNumber: Int,
    retiredParticipantNumber: Int?,
    winnerIconSize: Dp,
    retiredLabelFontSize: TextUnit
) {
    val contentDescription =
        if (winnerParticipantNumber == 1) "First participant won" else "Second participant won"
    Box(modifier = Modifier.then(modifier)) {
        if (participantNumber == winnerParticipantNumber) {
            WinnerSign(
                modifier = Modifier.fillMaxHeight(),
                winnerIconSize = winnerIconSize,
                contentDescription = contentDescription
            )
        } else {
            if (retiredParticipantNumber == null) {
                Spacer(modifier = Modifier.fillMaxHeight())
            } else {
                RetiredSign(
                    modifier = Modifier.fillMaxHeight(),
                    fontSize = retiredLabelFontSize
                )
            }
        }

    }
}

@Composable
fun RetiredSign(
    modifier: Modifier = Modifier,
    fontSize: TextUnit
) {
    Box(modifier = Modifier.then(modifier)) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Ret.",
            fontSize = fontSize,
            color = Color.White
        )
    }
}

@Composable
fun WinnerSign(
    modifier: Modifier = Modifier,
    winnerIconSize: Dp,
    contentDescription: String? = null
) {
    Box(modifier = Modifier.then(modifier)) {
        Icon(
            modifier = Modifier.size(winnerIconSize).align(Alignment.Center),
            imageVector = Icons.Default.Check,
            tint = Color.White,
            contentDescription = contentDescription
        )
    }

}