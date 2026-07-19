package com.bashkevich.tennisscorekeeper.components.match_details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ChooseServePanel
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.connection_with_scoreboard_lost

@Composable
fun ScoreboardControlPanel(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
    match: Match,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {
    Box(modifier = Modifier.then(modifier)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchStatusButton(
                match = match,
                onStatusChange = { status ->
                    onEvent(
                        MatchDetailsUiEvent.ChangeMatchStatus(
                            status = status
                        )
                    )
                }
            )

            when (match.status) {
                MatchStatus.NOT_STARTED -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ChooseServePanel(
                        modifier = Modifier.fillMaxWidth(),
                        match = match,
                        onFirstParticipantToServeChoose = { participantId ->
                            onEvent(
                                MatchDetailsUiEvent.SetFirstParticipantToServe(
                                    participantId = participantId
                                )
                            )
                        },
                        onFirstPlayerInPairToServeChoose = { playerId ->
                            onEvent(
                                MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                    playerId = playerId
                                )
                            )
                        }
                    )
                }

                MatchStatus.IN_PROGRESS -> {
                    ParticipantsPointsControlPanel(
                        modifier = Modifier.fillMaxWidth(),
                        match = match,
                        onUpdateScore = { participantId, scoreType ->
                            onEvent(
                                MatchDetailsUiEvent.UpdateScore(
                                    participantId = participantId,
                                    scoreType = scoreType
                                )
                            )
                        },
                        onUndoPoint = { onEvent(MatchDetailsUiEvent.UndoPoint) },
                        onRedoPoint = { onEvent(MatchDetailsUiEvent.RedoPoint) }
                    )
                }

                MatchStatus.PAUSED -> {
                    RetireParticipantPanel(
                        modifier = Modifier.fillMaxWidth(),
                        match = match,
                        onParticipantRetire = { participantId ->
                            onEvent(
                                MatchDetailsUiEvent.SetParticipantRetired(
                                    participantId = participantId
                                )
                            )
                        }
                    )
                }

                else -> {}
            }
        }
        if (connectionState == ConnectionState.Disconnected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(32.dp))
                    .border(
                        border = BorderStroke(width = 1.dp, Color.Black),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.connection_with_scoreboard_lost),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScoreboardControlPanelDisconnectedPreview() {
    ScoreboardControlPanel(
        connectionState = ConnectionState.Disconnected,
        match = SAMPLE_MATCH,
        onEvent = {}
    )
}