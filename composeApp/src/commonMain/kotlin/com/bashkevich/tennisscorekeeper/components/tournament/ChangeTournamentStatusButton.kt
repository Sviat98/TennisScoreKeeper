package com.bashkevich.tennisscorekeeper.components.tournament

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus

@Composable
fun ChangeTournamentStatusButton(
    modifier: Modifier = Modifier,
    status: TournamentStatus,
    onStatusChange: (TournamentStatus) -> Unit,
    uncompletedMatches: Int,
    participantsAmount: Int
) {
    Box(modifier = Modifier.then(modifier)){
        when {
            status == TournamentStatus.NOT_STARTED && participantsAmount > 1 -> {
                Button(
                    onClick = {
                        onStatusChange(TournamentStatus.IN_PROGRESS)
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Start tournament")
                }
            }

            status == TournamentStatus.IN_PROGRESS && uncompletedMatches < 1 -> {
                Button(
                    onClick = {
                        onStatusChange(TournamentStatus.COMPLETED)
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Finish tournament")
                }
            }
        }
    }
}
