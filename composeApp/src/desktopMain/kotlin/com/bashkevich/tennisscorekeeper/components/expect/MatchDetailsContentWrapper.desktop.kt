package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsContent
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent

@Composable
actual fun MatchDetailsContentWrapper(
    modifier: Modifier,
    state: MatchDetailsState,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {
    MatchDetailsContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = onEvent
    )
}