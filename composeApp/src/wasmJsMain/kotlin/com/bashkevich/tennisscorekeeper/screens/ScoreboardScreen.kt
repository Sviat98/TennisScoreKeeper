package com.bashkevich.tennisscorekeeper.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.MatchView
import com.bashkevich.tennisscorekeeper.model.match.SAMPLE_MATCH

@Composable
fun ScoreboardScreen(
    modifier: Modifier = Modifier,
    id: Int
) {

    Box(modifier = Modifier.then(modifier)
        .drawBehind {
            drawRect(
                color = Color.Transparent,
                size = this.size,
                blendMode = BlendMode.Clear
            )
        }
        .fillMaxSize()) {
        MatchView(
            modifier = Modifier.align(Alignment.Center),
            match = SAMPLE_MATCH)
    }
}