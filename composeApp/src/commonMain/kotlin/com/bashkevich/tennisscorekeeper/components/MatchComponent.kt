package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun MatchView(
    modifier: Modifier = Modifier,
    match: Match,
) {
    var columnHeight by remember { mutableStateOf(0.dp) }

    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant


    val hasFirstParticipantWonMatch = firstParticipant.isWinner
    val hasSecondParticipantWonMatch = secondParticipant.isWinner

    val isWinnerInMatch = hasFirstParticipantWonMatch || hasSecondParticipantWonMatch

    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.background(color = Color(0xFF142c6c))
        ) {
            Column(
                modifier = Modifier.height(columnHeight)
                    .padding(top = 8.dp, bottom = 8.dp, start = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (firstParticipant.isServing) Arrangement.Top else Arrangement.Bottom
            ) {
                if ((firstParticipant.isServing || secondParticipant.isServing) && !isWinnerInMatch) {
                    Box(
                        modifier = Modifier.size(8.dp).clip(
                            CircleShape
                        ).background(color = Color.Yellow)
                    )
                } else {
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            // Колонка для имен игроков (выравнивание по правому краю)
            val density = LocalDensity.current
            Column(
                modifier = Modifier.wrapContentWidth().onGloballyPositioned { layoutCoordinates ->
                    columnHeight = with(density) {
                        layoutCoordinates.size.height.toDp()
                    }
                },
                horizontalAlignment = Alignment.Start, // Выравнивание по левому краю
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = firstParticipant.displayName,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    if (hasFirstParticipantWonMatch) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            tint = Color.White,
                            contentDescription = "First player Won"
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = secondParticipant.displayName,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    if (hasSecondParticipantWonMatch) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            tint = Color.White,
                            contentDescription = "Second player Won"
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.width(4.dp)) // Отступ между сетами
            match.previousSets.forEachIndexed { index, prevSet ->
                // Колонка для счета (выравнивание по центру)

                val firstPlayerGamesWon = prevSet.firstParticipantGamesWon
                val secondPlayerGamesWon = prevSet.secondParticipantGamesWon

                val isFirstPlayerWon = firstPlayerGamesWon > secondPlayerGamesWon
                Column(
                    modifier = Modifier.height(columnHeight).width(28.dp).padding(vertical = 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = prevSet.firstParticipantGamesWon.toString(),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = if (isFirstPlayerWon) 1f else 0.5f)
                    )
                    Text(
                        text = prevSet.secondParticipantGamesWon.toString(),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = if (!isFirstPlayerWon) 1f else 0.5f)
                    )
                }
                if (index != match.previousSets.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp)) // Отступ между сетами
                }
            }
        }

        val currentGameStarted =
            match.currentGame.firstParticipantPointsWon != "0" || match.currentGame.secondParticipantPointsWon != "0"

        if (currentGameStarted || match.currentSet.firstParticipantGamesWon != 0 || match.currentSet.secondParticipantGamesWon != 0) {
            Column(
                modifier = Modifier.height(columnHeight).width(28.dp)
                    .border(width = 1.dp, color = Color(0xFF142c6c)).padding(vertical = 1.dp)
                    .background(color = Color.Yellow),
                horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.currentSet.firstParticipantGamesWon.toString(),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Divider(modifier = Modifier.fillMaxWidth(), color = Color(0xFF142c6c))
                Text(
                    text = match.currentSet.secondParticipantGamesWon.toString(),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        }

        if (currentGameStarted) {
            Box(
                modifier = Modifier.height(columnHeight).width(28.dp)
                    .background(color = Color(0xFF142c6c)).padding(vertical = 1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().background(color = Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = match.currentGame.firstParticipantPointsWon,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), color = Color(0xFF142c6c))
                    Text(
                        text = match.currentGame.secondParticipantPointsWon,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            }
        }
    }
}