package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.set_template.SetTemplateCombobox
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Composable
fun TournamentNameComponent(
    modifier: Modifier = Modifier,
    tournamentNameState: TextFieldState
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
            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = tournamentNameState,
                placeholder = { Text("Tournament name") },
            )
        }
    }
}