package com.bashkevich.tennisscorekeeper.components.match.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch

@Composable
fun ChooseServePanel(
    modifier: Modifier = Modifier,
    match: Match,
    onFirstParticipantToServeChoose: (String) -> Unit,
    onFirstPlayerInPairToServeChoose: (String) -> Unit
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val firstParticipantId = firstParticipant.id
    val secondParticipantId = secondParticipant.id

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    onFirstParticipantToServeChoose(firstParticipantId)
                },
            ) {
                Text("Participant 1 first serve")
            }
            Button(
                onClick = { onFirstParticipantToServeChoose(secondParticipantId) },
            ) {
                Text("Participant 2 first serve")
            }
        }
        if (firstParticipant is ParticipantInDoublesMatch && secondParticipant is ParticipantInDoublesMatch) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onFirstPlayerInPairToServeChoose(firstParticipant.firstPlayer.id)
                    },
                ) {
                    Text("Player 1.1 first serve")
                }
                Button(
                    onClick = {
                        onFirstPlayerInPairToServeChoose(firstParticipant.secondPlayer.id)
                    },
                ) {
                    Text("Player 1.2 first serve")
                }
                Button(
                    onClick = {
                        onFirstPlayerInPairToServeChoose(secondParticipant.firstPlayer.id)
                    },
                ) {
                    Text("Player 2.1 first serve")
                }
                Button(
                    onClick = {
                        onFirstPlayerInPairToServeChoose(secondParticipant.secondPlayer.id)
                    },
                ) {
                    Text("Player 2.2 first serve")
                }
            }
        }
    }

}