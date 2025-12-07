package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent

@Composable
actual fun MatchDetailsScoreboardWrapper(
    modifier: Modifier,
    match: Match,
    onEvent: (MatchDetailsUiEvent)->Unit
){
   MatchDetailsScoreboardView(
        modifier =Modifier.then(modifier),
        match = match,
    )
}