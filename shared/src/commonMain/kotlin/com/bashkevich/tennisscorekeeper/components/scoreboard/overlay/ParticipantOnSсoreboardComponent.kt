package com.bashkevich.tennisscorekeeper.components.scoreboard.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.bashkevich.tennisscorekeeper.model.theme.domain.LocalScoreboardTheme

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
    val theme = LocalScoreboardTheme.current
    val isServeColorSameAsMain = theme.serveColor == theme.mainTextColor
    Text(
        text = buildAnnotatedString {
            val firstPlayerColor =
                if (firstPlayer.isServingNow && showServingPlayer) theme.serveColor else theme.mainTextColor
            withStyle(SpanStyle(
                color = firstPlayerColor,
                fontWeight = if (firstPlayer.isServingNow && showServingPlayer && isServeColorSameAsMain) FontWeight.Bold else FontWeight.Normal
            )) {
                append(firstPlayerDisplayName)
            }
            append(" / ")
            val secondPlayerColor =
                if (secondPlayer.isServingNow && showServingPlayer) theme.serveColor else theme.mainTextColor
            withStyle(SpanStyle(
                color = secondPlayerColor,
                fontWeight = if (secondPlayer.isServingNow && showServingPlayer && isServeColorSameAsMain) FontWeight.Bold else FontWeight.Normal
            )) {
                append(secondPlayerDisplayName)
            }
        },
        fontSize = 20.sp,
        color = theme.mainTextColor
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
                    color = LocalScoreboardTheme.current.mainTextColor
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