package com.bashkevich.tennisscorekeeper.components.scoreboard_short

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toShortMatchDisplayFormat
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant

@Composable
fun ParticipantOnShortScoreboardView(modifier: Modifier = Modifier, match: ShortMatch) {

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ParticipantOnShortScoreboardRow(
            participant = firstParticipant,
        )
        ParticipantOnShortScoreboardRow(
            participant = secondParticipant,
        )
    }
}

@Composable
fun ParticipantOnShortScoreboardRow(
    modifier: Modifier = Modifier,
    participant: ParticipantInShortMatch,
) {
    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (participant) {
            is ParticipantInShortSinglesMatch -> {
                SinglesPlayerOnShortScoreboardView(
                    modifier = Modifier.widthIn(max = 200.dp),
                    player = participant.player
                )
            }

            is ParticipantInShortDoublesMatch -> {
                DoublesParticipantOnShortScoreboardView(
                    participant = participant
                )
            }
        }
    }
}

@Composable
fun DoublesParticipantOnShortScoreboardView(
    modifier: Modifier = Modifier,
    participant: ParticipantInShortDoublesMatch
) {
    Column(
        modifier = Modifier.then(modifier),
    ) {
        DoublesPlayerOnShortScoreboardView(
            player = participant.firstPlayer
        )
        DoublesPlayerOnShortScoreboardView(
            player = participant.secondPlayer
        )
    }
}

@Composable
fun SinglesPlayerOnShortScoreboardView(
    modifier: Modifier = Modifier,
    player: PlayerInParticipant,
) {
    BasicText(
        modifier = Modifier.then(modifier),
        text = player.toShortMatchDisplayFormat(),
        color = { Color.White },
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(maxFontSize = 16.sp, minFontSize = 10.sp, stepSize = 1.sp)
    )
}

@Composable
fun DoublesPlayerOnShortScoreboardView(
    modifier: Modifier = Modifier,
    player: PlayerInParticipant,
) {
    Text(
        modifier = Modifier.then(modifier),
        text = player.toShortMatchDisplayFormat(),
        color = Color.White,
        fontSize = 12.sp,
        lineHeight = 12.sp
    )
}