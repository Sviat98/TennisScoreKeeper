package com.bashkevich.tennisscorekeeper.components.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowForward
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

    val isWinnerInMatch = firstParticipant.isWinner || secondParticipant.isWinner

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
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            IconButton(
                onClick = onUndoPoint,
                enabled = hasFirstPointPlayed
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = "Undo point"
                )
            }
            IconButton(
                onClick = onRedoPoint,
                enabled = isPointShift
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowForward,
                    contentDescription = "Redo point"
                )
            }
        }
    }
}
