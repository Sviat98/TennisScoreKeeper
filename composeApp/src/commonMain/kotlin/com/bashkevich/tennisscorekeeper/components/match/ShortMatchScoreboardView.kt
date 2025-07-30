package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.scoreboard.CurrentGamePausedComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.PrevSetScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.SeedScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard_short.ParticipantOnShortScoreboardView
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.body.convertToString
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun ShortMatchScoreboardCard(
    modifier: Modifier = Modifier,
    match: ShortMatch,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.then(modifier),
        border = BorderStroke(1.dp, color = Color.Black),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(match.status.convertToString())
            ShortMatchScoreboardView(modifier = Modifier.fillMaxWidth(), match = match)
        }
    }

}

@Composable
fun ShortMatchScoreboardView(
    modifier: Modifier = Modifier,
    match: ShortMatch
) {
    var columnHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Row(
        modifier = Modifier.then(modifier).background(
            color = Color(0xFF142c6c)
        ).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            SeedScoreboardComponent(
                modifier = Modifier.height(columnHeight),
                firstParticipantSeed = match.firstParticipant.seed,
                secondParticipantSeed = match.secondParticipant.seed
            )
            ParticipantOnShortScoreboardView(
                modifier = Modifier.widthIn(min = 80.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        columnHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    }.padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 8.dp),
                match = match
            )
        }

        Row {
            match.previousSets.forEach { prevSet ->
                PrevSetScoreboardComponent(modifier = Modifier.height(columnHeight).width(32.dp), prevSet = prevSet, showWithoutHighlighting = false)
            }
            //currentSet и currentGame появляются ТОЛЬКО если матч в статусе PAUSED
            match.currentSet?.let { currentSet ->
                PrevSetScoreboardComponent(modifier = Modifier.height(columnHeight).width(32.dp), prevSet = currentSet, showWithoutHighlighting = true)
            }
            match.currentGame?.let {
                CurrentGamePausedComponent(modifier = Modifier.height(columnHeight).width(48.dp), currentGame = match.currentGame )
            }
        }

    }
}