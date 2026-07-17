package com.bashkevich.tennisscorekeeper.components.scoreboard.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ColorScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.MatchDetailsCurrentGameComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.CurrentSetComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.PrevSetScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.SeedScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.ServeScoreboardComponent
import com.bashkevich.tennisscorekeeper.components.scoreboard.components.WinnerAndRetiredParticipantComponent
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.theme.domain.LocalScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
fun MatchScoreboardView(
    modifier: Modifier = Modifier,
    match: Match,
    theme: ScoreboardTheme,
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val firstParticipantId = firstParticipant.id
    val secondParticipantId = secondParticipant.id

    val verticalPadding = 4.dp
    val extraPaddingFromCenter = 0.dp // если хотим увеличить расстояние между участниками

    val spaceBetweenParticipants = 2 * (verticalPadding+extraPaddingFromCenter)

    CompositionLocalProvider(LocalScoreboardTheme provides theme) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .then(modifier)
        ) {
            ColorScoreboardComponent(
                modifier = Modifier.fillMaxHeight().width(12.dp),
                match = match
            )
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .background(color = theme.mainBackgroundColor)
            ) {
                SeedScoreboardComponent(
                    modifier = Modifier.fillMaxHeight(),
                    firstParticipantSeed = match.firstParticipant.seed,
                    secondParticipantSeed = match.secondParticipant.seed,
                )
                ParticipantOnScoreboardView(
                    modifier = Modifier.widthIn(min = 80.dp)
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
                    modifier = Modifier.fillMaxHeight(),
                    firstParticipantId = firstParticipantId,
                    secondParticipantId = secondParticipantId,
                    winnerParticipantId = winnerParticipantId,
                    retiredParticipantId = retiredParticipantId
                )
                ServeScoreboardComponent(
                    modifier = Modifier.fillMaxHeight(),
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
                        modifier = Modifier.fillMaxHeight().aspectRatio(0.5f),
                        prevSet = prevSet,
                        retiredParticipantNumber = retiredParticipantNumber
                    )
                }
                val currentSet = match.currentSet
                currentSet?.let {
                    CurrentSetComponent(
                        modifier = Modifier.fillMaxHeight().aspectRatio(0.5f)
                            .padding(horizontal = 1.dp),
                        currentSet = currentSet
                    )
                }
                match.currentGame?.let {
                    MatchDetailsCurrentGameComponent(
                        modifier = Modifier.fillMaxHeight().aspectRatio(0.5f),
                        currentGame = match.currentGame
                    )
                }
            }
        }
    }
}
