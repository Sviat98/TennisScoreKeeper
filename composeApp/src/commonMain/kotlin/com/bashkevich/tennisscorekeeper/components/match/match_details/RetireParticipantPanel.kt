package com.bashkevich.tennisscorekeeper.components.match.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun RetireParticipantPanel(
    match: Match,
    onParticipantRetire: (String)-> Unit
){
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {onParticipantRetire(firstParticipant.id)}){
            Text("Participant 1 retire")
        }

        Button(onClick = {onParticipantRetire(secondParticipant.id)}){
            Text("Participant 2 retire")
        }
    }
}