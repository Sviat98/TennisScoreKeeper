package com.bashkevich.tennisscorekeeper.components.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.SinglesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant

@Composable
fun ParticipantCard(
    modifier: Modifier = Modifier,
    participant: TennisParticipant
) {
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val seed = participant.seed

        if (seed != null) {
            Text(seed.toString())
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        val participantInfo = when (participant) {
            is SinglesParticipant -> "${participant.player.surname} ${participant.player.name}"
            is DoublesParticipant -> "${participant.firstPlayer.surname} ${participant.firstPlayer.name} / ${participant.secondPlayer.surname} ${participant.secondPlayer.name}"
        }

        Text(participantInfo)
    }
}