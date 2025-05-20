package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.match.domain.DoublesPlayer
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.domain.SinglesParticipant
import com.bashkevich.tennisscorekeeper.model.match.domain.TennisParticipant

@Composable
fun ParticipantOnScoreboardView(modifier: Modifier = Modifier, match: Match) {

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ParticipantOnScoreboardRow(
            participant = firstParticipant,
        )
        ParticipantOnScoreboardRow(
            participant = secondParticipant,
        )
    }
}

@Composable
fun DoublesParticipantOnScoreboard(participant: DoublesParticipant) {
    val (firstPlayerDisplayName, secondPlayerDisplayName) = participant.displayName.split("/")

    val firstPlayer = participant.firstPlayer as DoublesPlayer
    val secondPlayer = participant.secondPlayer as DoublesPlayer

    Text(
        text = buildAnnotatedString {
            val firstPlayerColor = if (firstPlayer.isServing) Color.Yellow else Color.White
            withStyle(SpanStyle(color = firstPlayerColor)) {
                append(firstPlayerDisplayName)
            }
            append(" / ")
            val secondPlayerColor = if (secondPlayer.isServing) Color.Yellow else Color.White
            withStyle(SpanStyle(color = secondPlayerColor)) {
                append(secondPlayerDisplayName)
            }
        },
        fontSize = 20.sp,
        color = Color.White
    )
}

@Composable
fun ParticipantOnScoreboardRow(
    modifier: Modifier = Modifier,
    participant: TennisParticipant,
) {
    val hasParticipantWonMatch = participant.isWinner

    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (participant) {
            is SinglesParticipant -> {
                Text(
                    text = participant.displayName,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            is DoublesParticipant -> {
                DoublesParticipantOnScoreboard(
                    participant = participant,
                )
            }
        }

        if (hasParticipantWonMatch) {
            WinnerIcon(contentDescription = "Player won")
        }
    }
}

@Composable
fun WinnerIcon(contentDescription: String? = null) {
    Icon(
        imageVector = Icons.Default.Check,
        tint = Color.White,
        contentDescription = contentDescription
    )
}