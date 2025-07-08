package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.match.match_details.ChooseServePanel
import com.bashkevich.tennisscorekeeper.components.match.match_details.MatchStatusButton
import com.bashkevich.tennisscorekeeper.components.match.match_details.ParticipantsPointsControlPanel
import com.bashkevich.tennisscorekeeper.components.scoreboard.MatchView
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.convertToString

@Composable
fun MatchDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchDetailsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    MatchDetailsContent(
        modifier = Modifier.then(modifier).fillMaxSize(),
        state = state,
        onEvent = { viewModel.onEvent(it) }
    )
}

@Composable
fun MatchDetailsContent(
    modifier: Modifier = Modifier,
    state: MatchDetailsState,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {

    val match = state.match

    Column(
        modifier = Modifier.then(modifier).padding(all = 16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MatchView(
            match = match
        )

        Text("Status: ${match.status.convertToString()}")

        MatchStatusButton(
            match = match,
            onStatusChange = { status -> onEvent(MatchDetailsUiEvent.ChangeMatchStatus(status = status)) }
        )

        when (match.status) {
            MatchStatus.NOT_STARTED -> {
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

            else -> {}
        }


    }
}