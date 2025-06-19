package com.bashkevich.tennisscorekeeper.components.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun MatchView(
    modifier: Modifier = Modifier,
    match: Match,
) {
    var columnHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier.then(modifier).background(color = Color(0xFF142c6c))
    ) {
        val density = LocalDensity.current
        SeedScoreboardComponent(
            modifier = Modifier.height(columnHeight),
            match = match
        )
        ParticipantOnScoreboardView(
            modifier = Modifier.widthIn(min = 80.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    columnHeight = with(density) {
                        layoutCoordinates.size.height.toDp()
                    }
                }.padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 8.dp), match = match,
        )
        ServeScoreboardComponent(
            modifier = Modifier.height(columnHeight),
            match = match
        )
        Spacer(modifier = Modifier.width(4.dp))

        match.previousSets.forEach { prevSet ->
            // Колонка для счета (выравнивание по центру)

            PrevSetScoreboardComponent(
                modifier = Modifier.height(columnHeight).width(columnHeight / 2),
                prevSet = prevSet
            )
        }

        match.currentSet?.let {
            CurrentSetComponent(
                modifier = Modifier.height(columnHeight).width(columnHeight / 2),
                currentSet = match.currentSet
            )
            Spacer(modifier = Modifier.width(1.dp))
        }
        match.currentGame?.let{
            CurrentGameComponent(
                modifier = Modifier.height(columnHeight).width(columnHeight / 2),
                currentGame = match.currentGame
            )
        }
    }
}