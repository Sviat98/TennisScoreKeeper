package com.bashkevich.tennisscorekeeper.screens.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.scoreboard.overlay.MatchScoreboardView
import com.bashkevich.tennisscorekeeper.components.theme.ThemeLoadErrorRow
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardThemeState
import com.bashkevich.tennisscorekeeper.model.theme.domain.themeOrDefault
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.connection_with_scoreboard_lost

@Composable
fun ScoreboardScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreboardViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.then(modifier)
            .drawBehind {
                drawRect(
                    color = Color.Transparent,
                    size = this.size,
                    blendMode = BlendMode.Clear
                )
            }
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        val connectionState = state.connectionState
        if (connectionState == ConnectionState.Loading) {
            CircularProgressIndicator()
        } else {
            ScoreboardContent(
                match = state.match,
                theme = state.themeState.themeOrDefault()
            )
            if (state.themeState is ScoreboardThemeState.Error) {
                ThemeLoadErrorRow(onRetry = { viewModel.onEvent(ScoreboardUiEvent.RetryThemeLoad) })
            }
            SubcomposeLayout { constraints ->
                val textPlaceable = subcompose("text") {
                    Text(
                        text = stringResource(Res.string.connection_with_scoreboard_lost),
                        color = MaterialTheme.colorScheme.error,
                    )
                }.first().measure(constraints)

                val placeable = if (state.connectionState == ConnectionState.Disconnected) {
                    textPlaceable
                } else {
                    val spacerConstraints = constraints.copy(
                        minHeight = textPlaceable.height,
                        maxHeight = textPlaceable.height
                    )
                    subcompose("spacer") {
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }.first().measure(spacerConstraints)
                }

                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }
            }
        }
    }
}

@Composable
fun ScoreboardContent(
    modifier: Modifier = Modifier,
    match: Match,
    theme: ScoreboardTheme
) {
    Box(modifier = Modifier.then(modifier).size(1024.dp)) {
        MatchScoreboardView(
            modifier = Modifier.align(Alignment.CenterStart),
            match = match,
            theme = theme
        )
    }

}
