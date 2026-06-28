package com.bashkevich.tennisscorekeeper.components.expect

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent

@Composable
expect fun MatchDetailsContentWrapper(
    modifier: Modifier,
    state: MatchDetailsState,
    snackbarHostState: SnackbarHostState,
    onEvent: (MatchDetailsUiEvent) -> Unit
)