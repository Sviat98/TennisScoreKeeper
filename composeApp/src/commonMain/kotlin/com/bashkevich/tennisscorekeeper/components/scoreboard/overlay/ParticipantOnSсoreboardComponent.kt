package com.bashkevich.tennisscorekeeper.components.scoreboard.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch

@Composable
fun ParticipantOnScoreboardView(
    modifier: Modifier = Modifier,
    spaceBetweenParticipants: Dp = 0.dp, match: Match
) {

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val matchStatus = match.status

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(spaceBetweenParticipants)
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

@Suppress("SuspiciousIndentation")
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
            val firstPlayerColor =
                if (firstPlayer.isServingNow && showServingPlayer) Color.Yellow else Color.White
            withStyle(SpanStyle(color = firstPlayerColor)) {
                append(firstPlayerDisplayName)
            }
            append(" / ")
            val secondPlayerColor =
                if (secondPlayer.isServingNow && showServingPlayer) Color.Yellow else Color.White
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
    Box(modifier = Modifier.then(modifier)) {
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
    }
}