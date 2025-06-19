package com.bashkevich.tennisscorekeeper.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bashkevich.tennisscorekeeper.components.scoreboard.MatchView
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH


@Composable
@Preview
fun MatchViewPreview() {
    MatchView(modifier = Modifier, match = SAMPLE_MATCH)
}

@Composable
@Preview
fun DoublesMatchViewPreview() {
    MatchView(modifier = Modifier, match = DOUBLES_SAMPLE_MATCH)
}