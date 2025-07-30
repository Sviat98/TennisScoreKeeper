package com.bashkevich.tennisscorekeeper.components.scoreboard

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
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch

@Composable
fun ParticipantOnScoreboardView(modifier: Modifier = Modifier, match: Match) {

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val matchStatus = match.status

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ParticipantOnScoreboardRow(
            participant = firstParticipant,
            matchStatus = matchStatus
        )
        ParticipantOnScoreboardRow(
            participant = secondParticipant,
            matchStatus = matchStatus
        )
    }
}

@Composable
fun DoublesParticipantOnScoreboard(
    participant: ParticipantInDoublesMatch,
    showServingPlayer: Boolean
) {
    val (firstPlayerDisplayName, secondPlayerDisplayName) = participant.displayName.split("/")

    val firstPlayer = participant.firstPlayer as PlayerInDoublesMatch

    val secondPlayer = participant.secondPlayer as PlayerInDoublesMatch
        Text(
            text = buildAnnotatedString {
                val firstPlayerColor = if (firstPlayer.isServingNow && showServingPlayer) Color.Yellow else Color.White
                withStyle(SpanStyle(color = firstPlayerColor)) {
                    append(firstPlayerDisplayName)
                }
                append(" / ")
                val secondPlayerColor = if (secondPlayer.isServingNow && showServingPlayer) Color.Yellow else Color.White
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
    participant: TennisParticipantInMatch,
    matchStatus: MatchStatus
) {
    val hasParticipantWonMatch = participant.isWinner

    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (participant) {
            is ParticipantInSinglesMatch -> {
                Text(
                    text = participant.displayName,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            is ParticipantInDoublesMatch -> {
                // когда матч в статусе PAUSED, то не выделяем
                val showServingPlayer = matchStatus != MatchStatus.PAUSED

                DoublesParticipantOnScoreboard(
                    participant = participant,
                    showServingPlayer = showServingPlayer
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