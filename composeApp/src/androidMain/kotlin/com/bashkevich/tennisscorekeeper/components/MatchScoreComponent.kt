package com.bashkevich.tennisscorekeeper.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH


@Composable
@Preview
fun MatchViewPreview() {
    MatchView(modifier = Modifier, match = SAMPLE_MATCH)
}