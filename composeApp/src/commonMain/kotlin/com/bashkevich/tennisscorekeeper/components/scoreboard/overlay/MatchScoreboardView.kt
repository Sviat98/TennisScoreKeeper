package com.bashkevich.tennisscorekeeper.components.scoreboard.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.times
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ColorScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.CurrentGameInProgressComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.CurrentSetComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.PrevSetScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.SeedScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ServeScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.WinnerAndRetiredParticipantComponent
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun MatchScoreboardView(
    modifier: Modifier = Modifier,
    match: Match,
) {
    var columnHeight by remember { mutableStateOf(0.dp) }

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val firstParticipantId = firstParticipant.id
    val secondParticipantId = secondParticipant.id

    val verticalPadding = 4.dp
    val extraPaddingFromCenter = 0.dp // если хотим увеличить расстояние между участниками

    val spaceBetweenParticipants = 2 * (verticalPadding+extraPaddingFromCenter)

    Row(
        modifier = Modifier.then(modifier)
    ) {
        ColorScoreboardComponent(
            modifier = Modifier.height(columnHeight).width(12.dp),
            match = match
        )
        Row(
            modifier = Modifier.background(color = Color(0xFF142c6c))
        ) {
            val density = LocalDensity.current

            SeedScoreboardComponent(
                modifier = Modifier.height(columnHeight),
                firstParticipantSeed = match.firstParticipant.seed,
                secondParticipantSeed = match.secondParticipant.seed,
            )
            ParticipantOnScoreboardView(
                modifier = Modifier.widthIn(min = 80.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        columnHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    }
                    .padding(
                        top = verticalPadding,
                        bottom = verticalPadding,
                        start = 4.dp,
                        end = 8.dp
                    ),
                spaceBetweenParticipants = spaceBetweenParticipants,
                match = match,
            )
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
                retiredParticipantId = retiredParticipantId
            )
            ServeScoreboardComponent(
                modifier = Modifier.height(columnHeight),
                match = match
            )

            val prevSets = match.previousSets

            val lastSetIndex = prevSets.size - 1

            match.previousSets.forEachIndexed { index, prevSet ->
                // смотрим, есть ли досрочное завершение сета
                val retiredParticipantNumber = if (index == lastSetIndex) {
                    when {
                        firstParticipant.isRetired -> 1
                        secondParticipant.isRetired -> 2
                        else -> null
                    }
                } else null


                // Если сет закончился на счете 0:0 (остановка произошла в начале матча или после сыгранного сета), то его не выводим
                PrevSetScoreboardComponent(
                    modifier = Modifier.height(columnHeight).width(columnHeight / 2),
                    prevSet = prevSet,
                    retiredParticipantNumber = retiredParticipantNumber
                )
            }
            val currentSet = match.currentSet
            currentSet?.let {
                CurrentSetComponent(
                    modifier = Modifier.height(columnHeight).width(columnHeight / 2)
                        .padding(horizontal = 1.dp),
                    currentSet = currentSet
                )
//                when (match.status) {
//                    MatchStatus.IN_PROGRESS -> {
//                        CurrentSetComponent(
//                            modifier = Modifier.height(columnHeight).width(columnHeight / 2)
//                                .padding(horizontal = 1.dp),
//                            currentSet = currentSet
//                        )
//                    }
//
//                    MatchStatus.PAUSED -> {
//                        PrevSetScoreboardComponent(
//                            modifier = Modifier.height(columnHeight).width(columnHeight / 2),
//                            prevSet = currentSet,
//                            isSetFinished = false
//                        )
//                    }
//
//                    else -> {}
//                }
            }
            match.currentGame?.let {
//                if (match.status == MatchStatus.PAUSED) {
//                    CurrentGamePausedComponent(
//                        modifier = Modifier.height(columnHeight).wrapContentWidth(),
//                        currentGame = match.currentGame
//                    )
//                } else {
                CurrentGameInProgressComponent(
                    modifier = Modifier.height(columnHeight).width(columnHeight / 2),
                    currentGame = match.currentGame
                )
                //}
            }
        }
    }
}