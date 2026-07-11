package com.bashkevich.tennisscorekeeper.components.scoreboard.short

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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ShortMatchCurrentGameComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.PrevSetScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.SeedScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.WinnerAndRetiredParticipantComponent
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch
import com.bashkevich.tennisscorekeeper.model.match.remote.body.toResource
import com.bashkevich.tennisscorekeeper.model.theme.domain.LocalScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShortMatchScoreboardCard(
    modifier: Modifier = Modifier,
    match: ShortMatch,
    theme: ScoreboardTheme,
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
            Text(stringResource(match.status.toResource()))
            CompositionLocalProvider(LocalScoreboardTheme provides theme) {
                ShortMatchScoreboardView(modifier = Modifier.fillMaxWidth(), match = match)
            }
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
    val theme = LocalScoreboardTheme.current

    Row(
        modifier = Modifier.then(modifier).background(
            color = theme.mainBackgroundColor
        ).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val verticalPadding = 4.dp
        val extraPaddingFromCenter = 4.dp // если хотим увеличить расстояние между участниками

        val spaceBetweenParticipants = 2 * (verticalPadding+extraPaddingFromCenter)
        Row {
            SeedScoreboardComponent(
                modifier = Modifier.height(columnHeight),
                firstParticipantSeed = match.firstParticipant.seed,
                secondParticipantSeed = match.secondParticipant.seed,
                paddingFromCenter = verticalPadding
            )
            ParticipantOnShortScoreboardView(
                modifier = Modifier.widthIn(min = 80.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        columnHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    }
                    .padding(top = verticalPadding, bottom = verticalPadding, start = 4.dp, end = 8.dp),
                match = match,
                spaceBetweenParticipants = spaceBetweenParticipants
            )
        }

        val defaultFontSize = 16.sp

        val firstParticipant = match.firstParticipant
        val secondParticipant = match.secondParticipant

        val firstParticipantId = firstParticipant.id
        val secondParticipantId = secondParticipant.id

        val prevSets = match.previousSets

        val lastSetIndex = prevSets.size - 1

        Row {
            val winnerParticipantId = when{
                firstParticipant.isWinner -> firstParticipantId
                secondParticipant.isWinner -> secondParticipantId
                else -> null
            }

            val retiredParticipantId = when{
                firstParticipant.isRetired -> firstParticipantId
                secondParticipant.isRetired -> secondParticipantId
                else -> null
            }
            WinnerAndRetiredParticipantComponent(
                modifier = Modifier.height(columnHeight),
                firstParticipantId = firstParticipantId,
                secondParticipantId = secondParticipantId,
                winnerParticipantId = winnerParticipantId,
                retiredParticipantId = retiredParticipantId,
                paddingFromCenter = extraPaddingFromCenter,
                winnerIconSize = 20.dp,
                retiredLabelFontSize = defaultFontSize
            )
            prevSets.forEachIndexed { index, prevSet ->
                // смотрим, есть ли досрочное завершение сета
                val retiredParticipantNumber = if (index == lastSetIndex) {
                    when {
                        firstParticipant.isRetired -> 1
                        secondParticipant.isRetired -> 2
                        else -> null
                    }
                } else null

                PrevSetScoreboardComponent(
                    modifier = Modifier.height(columnHeight).padding(horizontal = 4.dp),
                    prevSet = prevSet,
                    numberFontSize = defaultFontSize,
                    retiredParticipantNumber = retiredParticipantNumber,
                    paddingFromCenter = extraPaddingFromCenter,
                )
            }

            //currentSet и currentGame появляются ТОЛЬКО если матч в статусе PAUSED
            match.currentSet?.let { currentSet ->
                PrevSetScoreboardComponent(
                    modifier = Modifier.height(columnHeight).padding(horizontal = 4.dp),
                    prevSet = currentSet,
                    numberFontSize = defaultFontSize,
                    isSetFinished = false,
                    paddingFromCenter = extraPaddingFromCenter
                )
            }
            match.currentGame?.let {
                ShortMatchCurrentGameComponent(
                    modifier = Modifier.height(columnHeight).width(48.dp),
                    currentGame = match.currentGame
                )
            }
        }

    }
}