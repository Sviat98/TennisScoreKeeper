package com.bashkevich.tennisscorekeeper.components.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantListItem

@Composable
fun ParticipantCard(
    modifier: Modifier = Modifier,
    participant: ParticipantListItem,
    maxSeedWidth: Dp,
    maxDisplayNameWidth: Dp,
) {
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val seed = participant.seedNumber
        val displayName = participant.displayName

        Text(
            text = seed,
            modifier = Modifier.width(maxSeedWidth),
            textAlign = TextAlign.End
        )
        Text(
            text = displayName,
            modifier = Modifier.width(maxDisplayNameWidth),
            textAlign = TextAlign.Start
        )
    }
}