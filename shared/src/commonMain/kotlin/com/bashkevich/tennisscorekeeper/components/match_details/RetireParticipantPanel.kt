package com.bashkevich.tennisscorekeeper.components.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.player_1_retire
import tennisscorekeeper.shared.generated.resources.player_2_retire

@Composable
fun RetireParticipantPanel(
    modifier: Modifier = Modifier,
    match: Match,
    onParticipantRetire: (Int)-> Unit
){
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {onParticipantRetire(firstParticipant.id)}){
            Text(stringResource(Res.string.player_1_retire))
        }

        Button(onClick = {onParticipantRetire(secondParticipant.id)}){
            Text(stringResource(Res.string.player_2_retire))
        }
    }
}