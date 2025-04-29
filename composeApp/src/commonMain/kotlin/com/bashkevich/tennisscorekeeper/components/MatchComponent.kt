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
import androidx.compose.material.Text
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
import com.bashkevich.tennisscorekeeper.model.match.Match

@Composable
fun MatchView(
    modifier: Modifier = Modifier,
    match: Match,
) {
    var columnHeight by remember { mutableStateOf(0.dp) }

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
                verticalArrangement = if (match.firstPlayer.isServing) Arrangement.Top else Arrangement.Bottom
            ) {
                if (match.firstPlayer.isServing || match.secondPlayer.isServing) {
                    Box(
                        modifier = Modifier.size(8.dp).clip(
                            CircleShape
                        ).background(color = Color.Yellow)
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
                Text(
                    text = match.firstPlayer.surname,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    text = match.secondPlayer.surname,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp)) // Отступ между сетами
            match.previousSets.forEachIndexed { index, prevSet ->
                // Колонка для счета (выравнивание по центру)

                val firstPlayerGamesWon = prevSet.firstPlayerGamesWon
                val secondPlayerGamesWon = prevSet.secondPlayerGamesWon

                val isFirstPlayerWon = firstPlayerGamesWon > secondPlayerGamesWon
                Column(
                    modifier = Modifier.height(columnHeight).width(28.dp).padding(vertical = 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = prevSet.firstPlayerGamesWon.toString(),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = if (isFirstPlayerWon) 1f else 0.5f)
                    )
                    Text(
                        text = prevSet.secondPlayerGamesWon.toString(),
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
            match.currentGame.firstPlayerPointsWon != "0" || match.currentGame.secondPlayerPointsWon != "0"

        if (currentGameStarted || match.currentSet.firstPlayerGamesWon != 0 || match.currentSet.secondPlayerGamesWon != 0){
            Column(
                modifier = Modifier.height(columnHeight).width(28.dp)
                    .border(width = 1.dp, color = Color(0xFF142c6c)).padding(vertical = 1.dp)
                    .background(color = Color.Yellow),
                horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.currentSet.firstPlayerGamesWon.toString(),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Divider(modifier = Modifier.fillMaxWidth(), color = Color(0xFF142c6c))
                Text(
                    text = match.currentSet.secondPlayerGamesWon.toString(),
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
                        text = match.currentGame.firstPlayerPointsWon,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), color = Color(0xFF142c6c))
                    Text(
                        text = match.currentGame.secondPlayerPointsWon,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            }
        }
    }
}