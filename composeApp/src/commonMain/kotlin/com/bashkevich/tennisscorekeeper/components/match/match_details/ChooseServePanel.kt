package com.bashkevich.tennisscorekeeper.components.match.match_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.participant.FirstServeParticipantCombobox
import com.bashkevich.tennisscorekeeper.components.player.ChooseFirstPlayerInPairToServeCombobox
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.participant.domain.PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PLAYER_IN_DOUBLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch

@Composable
fun ChooseServePanel(
    modifier: Modifier = Modifier,
    match: Match,
    onFirstParticipantToServeChoose: (String) -> Unit,
    onFirstPlayerInPairToServeChoose: (String) -> Unit
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val participantOptions = listOf(firstParticipant, secondParticipant)

    val firstParticipantToServe = participantOptions.firstOrNull { it.isServing } ?:PARTICIPANT_IN_SINGLES_MATCH_DEFAULT

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("First participant to serve")
            FirstServeParticipantCombobox(
                participantOptions = participantOptions,
                currentParticipant = firstParticipantToServe,
                onParticipantChange = { participant -> onFirstParticipantToServeChoose(participant.id) }
            )
        }
        if (firstParticipant is ParticipantInDoublesMatch && secondParticipant is ParticipantInDoublesMatch) {
            var nextParticipantToServeInDoublesMatch: ParticipantInDoublesMatch?
            var firstServePlayerOptions = emptyList<PlayerInDoublesMatch>()
            var nextServePlayerOptions = emptyList<PlayerInDoublesMatch>()
            var enablePlayerCombobox = false


            val firstParticipantToServeInDoublesMatch =
                firstParticipantToServe as? ParticipantInDoublesMatch
            firstParticipantToServeInDoublesMatch?.let {
                val nextParticipantToServe = participantOptions.first { !it.isServing }
                nextParticipantToServeInDoublesMatch =
                    nextParticipantToServe as ParticipantInDoublesMatch

                val firstParticipantToServeFirstPlayer =
                    firstParticipantToServeInDoublesMatch.firstPlayer as PlayerInDoublesMatch
                val firstParticipantToServeSecondPlayer =
                    firstParticipantToServeInDoublesMatch.secondPlayer as PlayerInDoublesMatch

                val nextParticipantToServeFirstPlayer =
                    nextParticipantToServeInDoublesMatch.firstPlayer as PlayerInDoublesMatch
                val nextParticipantToServeSecondPlayer =
                    nextParticipantToServeInDoublesMatch.secondPlayer as PlayerInDoublesMatch

                firstServePlayerOptions =
                    listOf(firstParticipantToServeFirstPlayer, firstParticipantToServeSecondPlayer)
                nextServePlayerOptions =
                    listOf(nextParticipantToServeFirstPlayer, nextParticipantToServeSecondPlayer)

                enablePlayerCombobox = true
            }

            val firstServePlayer = firstServePlayerOptions.firstOrNull { it.isServingNow } ?: PLAYER_IN_DOUBLES_MATCH_DEFAULT
            val nextServePlayer = nextServePlayerOptions.firstOrNull { it.isServingNext } ?: PLAYER_IN_DOUBLES_MATCH_DEFAULT

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("First player to serve")
                ChooseFirstPlayerInPairToServeCombobox(
                    playerOptions = firstServePlayerOptions,
                    currentPlayer = firstServePlayer,
                    onPlayerChange = { player -> onFirstPlayerInPairToServeChoose(player.id) },
                    enabled = enablePlayerCombobox
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Next player to serve")
                ChooseFirstPlayerInPairToServeCombobox(
                    playerOptions = nextServePlayerOptions,
                    currentPlayer = nextServePlayer,
                    onPlayerChange = { player -> onFirstPlayerInPairToServeChoose(player.id) },
                    enabled = enablePlayerCombobox
                )
            }
        }
    }
}