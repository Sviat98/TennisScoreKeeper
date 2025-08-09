package com.bashkevich.tennisscorekeeper.components.match.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun RetireParticipantPanel(
    modifier: Modifier = Modifier,
    match: Match,
    onParticipantRetire: (String)-> Unit
){
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {onParticipantRetire(firstParticipant.id)}){
            Text("Player 1 retire")
        }

        Button(onClick = {onParticipantRetire(secondParticipant.id)}){
            Text("Player 2 retire")
        }
    }
}