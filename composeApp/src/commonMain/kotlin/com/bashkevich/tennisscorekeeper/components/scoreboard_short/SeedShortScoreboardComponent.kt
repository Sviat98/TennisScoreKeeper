package com.bashkevich.tennisscorekeeper.components.scoreboard_short

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.scoreboard.SeedNumber
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch

@Composable
fun SeedShortScoreboardComponent(
    modifier: Modifier = Modifier,
    match: ShortMatch
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val firstParticipantSeed = firstParticipant.seed ?: 0
    val secondParticipantSeed = secondParticipant.seed ?: 0

    var maxSeedWidth by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.then(modifier).padding(start = if (maxSeedWidth > 0) 4.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SeedNumber(
            modifier = Modifier.weight(1f).onGloballyPositioned { layoutCoordinates ->

                val currentWidth = layoutCoordinates.size.width

                if (currentWidth > maxSeedWidth) {
                    maxSeedWidth = currentWidth
                }
            },
            seedNumber = firstParticipantSeed
        )
        SeedNumber(
            modifier = Modifier.weight(1f).onGloballyPositioned { layoutCoordinates ->

                val currentWidth = layoutCoordinates.size.width

                if (currentWidth > maxSeedWidth) {
                    maxSeedWidth = currentWidth
                }
            },
            seedNumber = secondParticipantSeed
        )
    }

}