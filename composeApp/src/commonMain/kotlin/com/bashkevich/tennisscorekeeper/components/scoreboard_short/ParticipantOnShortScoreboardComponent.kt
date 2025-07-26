package com.bashkevich.tennisscorekeeper.components.scoreboard_short

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.components.scoreboard.WinnerIcon
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInShortSinglesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.toShortMatchDisplayFormat
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant

@Composable
fun ParticipantOnShortScoreboardView(modifier: Modifier = Modifier, match: ShortMatch) {

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val isMatchCompleted = match.status == MatchStatus.COMPLETED

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ParticipantOnShortScoreboardRow(
            participant = firstParticipant,
            isMatchCompleted = isMatchCompleted
        )
        ParticipantOnShortScoreboardRow(
            participant = secondParticipant,
            isMatchCompleted = isMatchCompleted
        )
    }
}

@Composable
fun ParticipantOnShortScoreboardRow(
    modifier: Modifier = Modifier,
    participant: ParticipantInShortMatch,
    isMatchCompleted: Boolean
) {
    val hasParticipantWonMatch = participant.isWinner

    val showWinningSign = hasParticipantWonMatch && isMatchCompleted

    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when(participant){
            is ParticipantInShortSinglesMatch -> {
                PlayerOnShortScoreboardView(
                    player = participant.player
                )
            }
            is ParticipantInShortDoublesMatch ->{
                DoublesParticipantOnShortScoreboardView(
                    participant = participant
                )
            }
        }

        if (showWinningSign) {
            WinnerIcon(contentDescription = "Player won")
        }
    }
}

@Composable
fun DoublesParticipantOnShortScoreboardView(
    participant: ParticipantInShortDoublesMatch
){
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        PlayerOnShortScoreboardView(
            player = participant.firstPlayer
        )
        PlayerOnShortScoreboardView(
            player = participant.secondPlayer
        )
    }
}

@Composable
fun PlayerOnShortScoreboardView(
    modifier: Modifier = Modifier,
    player: PlayerInParticipant,
) {
    Text(
        modifier = Modifier.then(modifier),
        text = player.toShortMatchDisplayFormat(),
        fontSize = 20.sp,
        color = Color.White
    )
}