package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.tournament_name

@Composable
fun TournamentNameComponent(
    modifier: Modifier = Modifier,
    tournamentNameState: TextFieldState,
    // false = просмотр без правок (COMPLETED-турнир), имя readOnly
    canEditName: Boolean
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = tournamentNameState,
            readOnly = !canEditName,
            placeholder = { Text(stringResource(Res.string.tournament_name)) },
        )
    }
}