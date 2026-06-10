package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType

@Composable
fun TournamentTypeComponent(
    modifier: Modifier = Modifier,
    currentTournamentType: TournamentType?,
    onTournamentTypeChange: (TournamentType) -> Unit,
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.widthIn(max = 300.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TournamentTypeCombobox(
                modifier = Modifier.fillMaxWidth(),
                currentTournamentType = currentTournamentType,
                onTournamentTypeChange = onTournamentTypeChange,
            )
        }
    }
}