package com.bashkevich.tennisscorekeeper.screens.scoreboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.match.MatchScoreboardView
import com.bashkevich.tennisscorekeeper.model.match.domain.Match

@Composable
fun ScoreboardScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreboardViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    Box(modifier = Modifier.then(modifier)
        .drawBehind {
            drawRect(
                color = Color.Transparent,
                size = this.size,
                blendMode = BlendMode.Clear
            )
        }
        .fillMaxSize()) {
            ScoreboardContent(
                modifier = Modifier.align(Alignment.Center),
                match = state.match
            )
    }
}

@Composable
fun ScoreboardContent(
    modifier: Modifier = Modifier,
    match: Match
){
    Box(modifier = Modifier.then(modifier).size(1024.dp)){
        MatchScoreboardView(
            modifier = Modifier.align(Alignment.CenterStart),
            match = match
        )
    }

}