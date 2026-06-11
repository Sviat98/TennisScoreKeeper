package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentNameAndTypeComponent(
    modifier: Modifier = Modifier,
    tournamentNameState: TextFieldState,
    currentTournamentType: TournamentType?,
    onTournamentTypeChange: (TournamentType) -> Unit,
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val isWideScreen = windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Box(
       modifier = Modifier.then(modifier)
    ){
        if (isWideScreen) {
            Row(
                modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TournamentNameComponent(
                    modifier = Modifier.weight(1f),
                    tournamentNameState = tournamentNameState
                )
                TournamentTypeComponent(
                    modifier = Modifier.weight(1f),
                    currentTournamentType = currentTournamentType,
                    onTournamentTypeChange = onTournamentTypeChange,
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TournamentNameComponent(
                    modifier = Modifier.fillMaxWidth(),
                    tournamentNameState = tournamentNameState
                )
                TournamentTypeComponent(
                    modifier = Modifier.fillMaxWidth(),
                    currentTournamentType = currentTournamentType,
                    onTournamentTypeChange = onTournamentTypeChange,
                )
            }
        }
    }

}
