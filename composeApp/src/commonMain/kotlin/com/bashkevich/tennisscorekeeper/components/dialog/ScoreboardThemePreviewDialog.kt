package com.bashkevich.tennisscorekeeper.components.dialog

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme

@Composable
expect fun ScoreboardThemePreviewDialog(
    onDismissRequest: () -> Unit,
    theme: ScoreboardTheme
)

@Composable
fun ScoreboardThemePreviewContent(
    modifier: Modifier = Modifier,
    theme: ScoreboardTheme
) {
    Box(
        modifier = Modifier.then(modifier).background(Color.White).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        MatchDetailsScoreboardView(
            match = DOUBLES_SAMPLE_MATCH,
            theme = theme
        )
    }
}