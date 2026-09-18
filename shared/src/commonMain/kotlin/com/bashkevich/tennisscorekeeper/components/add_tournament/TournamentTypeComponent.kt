package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

@Composable
fun TournamentTypeComponent(
    modifier: Modifier = Modifier,
    currentTournamentType: TournamentType?,
    onTournamentTypeChange: (TournamentType) -> Unit,
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.Start
    ) {
        TournamentTypeCombobox(
            modifier = Modifier.fillMaxWidth(),
            currentTournamentType = currentTournamentType,
            onTournamentTypeChange = onTournamentTypeChange,
        )
    }
}