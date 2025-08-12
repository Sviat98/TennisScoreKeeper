package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WinnerAndRetiredParticipantComponent(
    modifier: Modifier = Modifier,
    firstParticipantId: String,
    secondParticipantId: String,
    winnerParticipantId: String?,
    retiredParticipantId: String?,
    paddingFromCenter: Dp = 0.dp
) {
    val hasParticipantWon = winnerParticipantId!=null

    if (hasParticipantWon) {
        Column(
            modifier = Modifier.then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val winnerParticipantNumber = if (winnerParticipantId == firstParticipantId) 1 else 2

            val retiredParticipantNumber = when(retiredParticipantId){
                firstParticipantId ->1
                secondParticipantId ->2
                else -> null
            }

            WinnerRetiredContainer(
                modifier = Modifier.weight(1f).padding(bottom = paddingFromCenter),
                participantNumber = 1,
                winnerParticipantNumber = winnerParticipantNumber,
                retiredParticipantNumber = retiredParticipantNumber
            )
            WinnerRetiredContainer(
                modifier = Modifier.weight(1f).padding(top = paddingFromCenter),
                participantNumber = 2,
                winnerParticipantNumber = winnerParticipantNumber,
                retiredParticipantNumber = retiredParticipantNumber
            )
        }
    }
}

@Composable
fun WinnerRetiredContainer(
    modifier: Modifier = Modifier,
    participantNumber: Int,
    winnerParticipantNumber: Int,
    retiredParticipantNumber: Int?
) {
    val contentDescription =
        if (winnerParticipantNumber == 1) "First participant won" else "Second participant won"
    Box(modifier = Modifier.then(modifier)) {
        if (participantNumber == winnerParticipantNumber) {
            WinnerSign(
                contentDescription = contentDescription
            )
        }else{
            if (retiredParticipantNumber == null){
                Spacer(modifier = Modifier.fillMaxHeight())
            }else{
                RetiredSign()
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