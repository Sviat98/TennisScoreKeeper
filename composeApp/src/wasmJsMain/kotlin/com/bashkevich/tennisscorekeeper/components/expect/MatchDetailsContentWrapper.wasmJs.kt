package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsCommonContent
import androidx.compose.material3.SnackbarHostState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent

@Composable
actual fun MatchDetailsContentWrapper(
    modifier: Modifier,
    state: MatchDetailsState,
    snackbarHostState: SnackbarHostState,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {
    MatchDetailsCommonContent(
        modifier = Modifier.then(modifier),
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = onEvent
    )
}