package com.bashkevich.tennisscorekeeper.components.scoreboard.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch

@Composable
fun ParticipantOnScoreboardDetailsView(
    modifier: Modifier = Modifier,
    spaceBetweenParticipants: Dp = 0.dp,
    match: Match
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(spaceBetweenParticipants)
    ) {
        ParticipantOnScoreboardDetailsRow(
            participant = firstParticipant,
        )
        ParticipantOnScoreboardDetailsRow(
            participant = secondParticipant,
        )
    }
}

@Composable
fun ParticipantOnScoreboardDetailsRow(
    modifier: Modifier = Modifier,
    participant: TennisParticipantInMatch,
) {
    Box(modifier = Modifier.then(modifier)) {
        when (participant) {
            is ParticipantInSinglesMatch -> {
                SinglesPlayerOnScoreboardDetailsView(
                    modifier = Modifier.widthIn(max = 150.dp),
                    participant = participant
                )
            }

            is ParticipantInDoublesMatch -> {
                DoublesParticipantOnScoreboardDetails(
                    participant = participant,
                )
            }
        }
    }
}

@Composable
fun SinglesPlayerOnScoreboardDetailsView(
    modifier: Modifier = Modifier,
    participant: TennisParticipantInMatch,
) {
    val textMeasurer = rememberTextMeasurer()

    val density = LocalDensity.current

    val participantDisplayFormat = participant.displayName

    val textStyle = TextStyle(fontSize = 16.sp)

    val playerTextHeight = with(density) {
        textMeasurer.measure(
            text = participantDisplayFormat,
            style = textStyle
        ).size.height.toDp()
    }
    Box(modifier = Modifier.then(modifier).height(playerTextHeight)) {
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            text = participantDisplayFormat,
            color = { Color.White },
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                maxFontSize = 16.sp,
                minFontSize = 11.sp,
                stepSize = 1.sp
            )
        )
    }

}


@Suppress("SuspiciousIndentation")
@Composable
fun DoublesParticipantOnScoreboardDetails(
    participant: ParticipantInDoublesMatch,
) {
    val (firstPlayerDisplayName, secondPlayerDisplayName) = participant.displayName.split("/")

    val firstPlayer = participant.firstPlayer as PlayerInDoublesMatch

    val secondPlayer = participant.secondPlayer as PlayerInDoublesMatch
    Text(
        text = buildAnnotatedString {
            val firstPlayerColor =
                if (firstPlayer.isServingNow) Color.Yellow else Color.White
            withStyle(SpanStyle(color = firstPlayerColor)) {
                append(firstPlayerDisplayName)
            }
            append(" /\n")
            val secondPlayerColor =
                if (secondPlayer.isServingNow) Color.Yellow else Color.White
            withStyle(SpanStyle(color = secondPlayerColor)) {
                append(secondPlayerDisplayName)
            }
        },
        fontSize = 12.sp,
        color = Color.White,
        lineHeight = 12.sp
    )
}

