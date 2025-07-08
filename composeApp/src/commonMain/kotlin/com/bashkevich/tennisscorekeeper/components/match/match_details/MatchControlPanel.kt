package com.bashkevich.tennisscorekeeper.components.match.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType

@Composable
fun ParticipantsPointsControlPanel(
    modifier: Modifier = Modifier,
    match: Match,
    onUpdateScore: (String, ScoreType) -> Unit,
    onUndoPoint: () -> Unit,
    onRedoPoint: () -> Unit
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val firstParticipantId = firstParticipant.id
    val secondParticipantId = secondParticipant.id

    val isWinnerInMatch = firstParticipant.isWinner || firstParticipant.isWinner

    val isGameStarted = match.currentGame != null

    val isSuperTiebreak = match.currentSetMode == SpecialSetMode.SUPER_TIEBREAK

    val hasFirstPointPlayed =
        match.previousSets.isNotEmpty() || match.currentSet != null || isGameStarted

    val isPointShift = match.pointShift < 0

    Column(
        modifier = Modifier.then(modifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onUpdateScore(firstParticipantId, ScoreType.POINT) },
                enabled = !isWinnerInMatch
            ) {
                Text("Player 1 Point")
            }
            Button(
                onClick = {
                    onUpdateScore(secondParticipantId, ScoreType.POINT)
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
                onClick = { onUpdateScore(firstParticipantId, ScoreType.GAME) },
                // нужно залочить, когда начался гейм, когда определился победитель и во время супер-тайбрейка
                enabled = !isGameStarted && !isWinnerInMatch && !isSuperTiebreak
            ) {
                Text("Player 1 Game")
            }
            Button(
                onClick = { onUpdateScore(secondParticipantId, ScoreType.GAME) },
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
                onClick = onUndoPoint,
                enabled = hasFirstPointPlayed
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Undo point"
                )
            }
            IconButton(
                onClick = onRedoPoint,
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
