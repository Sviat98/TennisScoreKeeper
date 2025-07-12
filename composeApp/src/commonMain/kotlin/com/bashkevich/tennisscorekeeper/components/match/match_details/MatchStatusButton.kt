package com.bashkevich.tennisscorekeeper.components.match.match_details

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch

@Composable
fun MatchStatusButton(
    match: Match,
    onStatusChange: (MatchStatus) -> Unit
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val isWinnerInMatch = firstParticipant.isWinner || firstParticipant.isWinner

    val enableStartMatchButton =
        if (firstParticipant is ParticipantInDoublesMatch && secondParticipant is ParticipantInDoublesMatch) {
            val firstParticipantFirstPlayer =
                firstParticipant.firstPlayer as PlayerInDoublesMatch
            val firstParticipantSecondPlayer =
                firstParticipant.secondPlayer as PlayerInDoublesMatch
            val secondParticipantFirstPlayer =
                secondParticipant.firstPlayer as PlayerInDoublesMatch
            val secondParticipantSecondPlayer =
                secondParticipant.secondPlayer as PlayerInDoublesMatch

            val isFirstServingPlayerInFirstParticipantSet = if (firstParticipant.isServing) {
                firstParticipantFirstPlayer.isServingNow || firstParticipantSecondPlayer.isServingNow
            }else{
                firstParticipantFirstPlayer.isServingNext || firstParticipantSecondPlayer.isServingNext
            }

            val isFirstServingPlayerInSecondParticipantSet = if (secondParticipant.isServing) {
                secondParticipantFirstPlayer.isServingNow || secondParticipantSecondPlayer.isServingNow
            }else{
                secondParticipantFirstPlayer.isServingNext || secondParticipantSecondPlayer.isServingNext
            }

            (firstParticipant.isServing || secondParticipant.isServing) && isFirstServingPlayerInFirstParticipantSet && isFirstServingPlayerInSecondParticipantSet
        } else {
            (firstParticipant.isServing || secondParticipant.isServing)
        }

    when (match.status) {
        MatchStatus.NOT_STARTED -> {
            Button(
                onClick = {
                    onStatusChange(MatchStatus.IN_PROGRESS)
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
                        onStatusChange(MatchStatus.COMPLETED)
                    },
                ) {
                    Text("Finish match")
                }
            } else {
                Button(
                    onClick = {
                        onStatusChange(MatchStatus.PAUSED)
                    },
                ) {
                    Text("Pause match")
                }
            }
        }

        MatchStatus.PAUSED -> {
            Button(
                onClick = { onStatusChange(MatchStatus.IN_PROGRESS) },
            ) {
                Text("Resume match")
            }
        }

        else -> {}
    }
}
