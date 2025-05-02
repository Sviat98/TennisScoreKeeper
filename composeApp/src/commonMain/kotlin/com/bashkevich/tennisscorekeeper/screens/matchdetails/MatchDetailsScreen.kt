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
import com.bashkevich.tennisscorekeeper.components.MatchView
import com.bashkevich.tennisscorekeeper.model.match.EMPTY_TENNIS_GAME
import com.bashkevich.tennisscorekeeper.model.match.EMPTY_TENNIS_SET
import com.bashkevich.tennisscorekeeper.model.match.remote.ScoreType
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode

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

    val isGameStarted = match.currentGame != EMPTY_TENNIS_GAME

    val firstPlayer = match.firstPlayer
    val secondPlayer = match.secondPlayer

    val isSuperTiebreak = match.currentSet.specialSetMode == SpecialSetMode.SUPER_TIEBREAK

    val isWinnerInMatch = firstPlayer.isWinner || secondPlayer.isWinner

    val hasFirstPointPlayed =
        match.previousSets.isNotEmpty() || match.currentSet != EMPTY_TENNIS_SET || isGameStarted

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
        val firstPlayerId = match.firstPlayer.id
        val secondPlayerId = match.secondPlayer.id

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    onEvent(
                        MatchDetailsUiEvent.UpdateScore(
                            matchId = matchId,
                            playerId = firstPlayerId,
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
                            playerId = secondPlayerId,
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
                            playerId = firstPlayerId,
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
                            playerId = secondPlayerId,
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
}