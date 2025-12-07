package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent
@Composable
expect fun MatchDetailsScoreboardWrapper(
    modifier: Modifier = Modifier,
    match: Match,
    onEvent: (MatchDetailsUiEvent)->Unit = {}
)