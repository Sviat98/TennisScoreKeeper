package com.bashkevich.tennisscorekeeper.screens.matchdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.components.scoreboard.MatchView
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.convertToString
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.DoublesPlayerInMatch

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

    val isGameStarted = match.currentGame != null

    val firstPlayer = match.firstParticipant
    val secondPlayer = match.secondParticipant

    val isSuperTiebreak = match.currentSetMode == SpecialSetMode.SUPER_TIEBREAK

    val isWinnerInMatch = firstPlayer.isWinner || secondPlayer.isWinner

    val hasFirstPointPlayed =
        match.previousSets.isNotEmpty() || match.currentSet != null || isGameStarted

    val isPointShift = match.pointShift < 0
    Column(
        modifier = Modifier.then(modifier).padding(all = 16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MatchView(
            match = match
        )
        val matchId = match.id

        val firstParticipant = match.firstParticipant
        val secondParticipant = match.secondParticipant

        val firstParticipantId = firstParticipant.id
        val secondParticipantId = secondParticipant.id

        Text("Status: ${match.status.convertToString()}")

        val enableStartMatchButton =
            if (firstParticipant is ParticipantInDoublesMatch && secondParticipant is ParticipantInDoublesMatch) {
                val firstParticipantFirstPlayer = firstParticipant.firstPlayer as DoublesPlayerInMatch
                val firstParticipantSecondPlayer = firstParticipant.secondPlayer as DoublesPlayerInMatch
                val secondParticipantFirstPlayer = secondParticipant.firstPlayer as DoublesPlayerInMatch
                val secondParticipantSecondPlayer = secondParticipant.secondPlayer as DoublesPlayerInMatch

                (firstParticipant.isServing || secondParticipant.isServing) && (firstParticipantFirstPlayer.isServing || firstParticipantSecondPlayer.isServing)
                        && (secondParticipantFirstPlayer.isServing || secondParticipantSecondPlayer.isServing)
            } else {
                (firstParticipant.isServing || secondParticipant.isServing)
            }

        when (match.status) {
            MatchStatus.NOT_STARTED -> {
                Button(
                    onClick = {
                        onEvent(
                            MatchDetailsUiEvent.ChangeMatchStatus(
                                matchId,
                                MatchStatus.IN_PROGRESS
                            )
                        )
                    },
                    enabled = enableStartMatchButton
                ) {
                    Text("Start match")
                }
            }
            MatchStatus.IN_PROGRESS -> {
                if (isWinnerInMatch) {
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.ChangeMatchStatus(
                                    matchId,
                                    MatchStatus.COMPLETED
                                )
                            )
                        },
                    ) {
                        Text("Finish match")
                    }
                } else {
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.ChangeMatchStatus(
                                    matchId,
                                    MatchStatus.PAUSED
                                )
                            )
                        },
                    ) {
                        Text("Pause match")
                    }
                }
            }
            MatchStatus.PAUSED -> {
                Button(
                    onClick = {
                        onEvent(
                            MatchDetailsUiEvent.ChangeMatchStatus(
                                matchId,
                                MatchStatus.IN_PROGRESS
                            )
                        )
                    },
                ) {
                    Text("Resume match")
                }
            }
            else->{}
        }

        when (match.status) {
            MatchStatus.NOT_STARTED -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.SetFirstParticipantToServe(
                                    matchId = matchId,
                                    participantId = firstParticipantId,
                                )
                            )
                        },
                    ) {
                        Text("Participant 1 first serve")
                    }
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.SetFirstParticipantToServe(
                                    matchId = matchId,
                                    participantId = secondParticipantId,
                                )
                            )
                        },
                    ) {
                        Text("Participant 2 first serve")
                    }
                }
                if (firstParticipant is ParticipantInDoublesMatch && secondParticipant is ParticipantInDoublesMatch) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                onEvent(
                                    MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                        matchId = matchId,
                                        playerId = firstParticipant.firstPlayer.id,
                                    )
                                )
                            },
                        ) {
                            Text("Player 1.1 first serve")
                        }
                        Button(
                            onClick = {
                                onEvent(
                                    MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                        matchId = matchId,
                                        playerId = firstParticipant.secondPlayer.id,
                                    )
                                )
                            },
                        ) {
                            Text("Player 1.2 first serve")
                        }
                        Button(
                            onClick = {
                                onEvent(
                                    MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                        matchId = matchId,
                                        playerId = secondParticipant.firstPlayer.id,
                                    )
                                )
                            },
                        ) {
                            Text("Player 2.1 first serve")
                        }
                        Button(
                            onClick = {
                                onEvent(
                                    MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                        matchId = matchId,
                                        playerId = secondParticipant.secondPlayer.id,
                                    )
                                )
                            },
                        ) {
                            Text("Player 2.2 first serve")
                        }
                    }
                }
            }

            MatchStatus.IN_PROGRESS -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.UpdateScore(
                                    matchId = matchId,
                                    playerId = firstParticipantId,
                                    scoreType = ScoreType.POINT
                                )
                            )
                        },
                        enabled = !isWinnerInMatch
                    ) {
                        Text("Player 1 Point")
                    }
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.UpdateScore(
                                    matchId = matchId,
                                    playerId = secondParticipantId,
                                    scoreType = ScoreType.POINT
                                )
                            )
                        },
                        enabled = !isWinnerInMatch
                    ) {
                        Text("Player 2 Point")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.UpdateScore(
                                    matchId = matchId,
                                    playerId = firstParticipantId,
                                    scoreType = ScoreType.GAME
                                )
                            )
                        },
                        // нужно залочить, когда начался гейм, когда определился победитель и во время супер-тайбрейка
                        enabled = !isGameStarted && !isWinnerInMatch && !isSuperTiebreak
                    ) {
                        Text("Player 1 Game")
                    }
                    Button(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.UpdateScore(
                                    matchId = matchId,
                                    playerId = secondParticipantId,
                                    scoreType = ScoreType.GAME
                                )
                            )
                        },
                        // нужно залочить, когда начался гейм, когда определился победитель и во время супер-тайбрейка
                        enabled = !isGameStarted && !isWinnerInMatch && !isSuperTiebreak
                    ) {
                        Text("Player 2 Game")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.UndoPoint(
                                    matchId = matchId
                                )
                            )
                        },
                        enabled = hasFirstPointPlayed
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Undo point"
                        )
                    }
                    IconButton(
                        onClick = {
                            onEvent(
                                MatchDetailsUiEvent.RedoPoint(
                                    matchId = matchId
                                )
                            )
                        },
                        enabled = isPointShift
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Redo point"
                        )
                    }
                }
            }
            else -> {}
        }


    }
}