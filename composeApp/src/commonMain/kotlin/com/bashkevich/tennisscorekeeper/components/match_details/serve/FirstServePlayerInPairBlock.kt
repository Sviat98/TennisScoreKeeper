package com.bashkevich.tennisscorekeeper.components.match_details.serve

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch
import com.bashkevich.tennisscorekeeper.model.player.domain.PLAYER_IN_DOUBLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInDoublesMatch

@Composable
fun FirstServePlayerInPairBlock(
    modifier: Modifier = Modifier,
    participantOptions: List<TennisParticipantInMatch>,
    firstParticipantToServe: TennisParticipantInMatch,
    onFirstPlayerInPairToServeChoose: (String)->Unit,
){
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

    val firstServePlayer = firstServePlayerOptions.firstOrNull { it.isServingNow }
        ?: PLAYER_IN_DOUBLES_MATCH_DEFAULT
    val nextServePlayer = nextServePlayerOptions.firstOrNull { it.isServingNext }
        ?: PLAYER_IN_DOUBLES_MATCH_DEFAULT
    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        FirstServePlayerInPairComponent(
            label = "First player to serve",
            playerOptions = firstServePlayerOptions,
            currentPlayer = firstServePlayer,
            onPlayerChange = { player -> onFirstPlayerInPairToServeChoose(player.id) },
            enabled = enablePlayerCombobox
        )

        FirstServePlayerInPairComponent(
            label = "Next player to serve",
            playerOptions = nextServePlayerOptions,
            currentPlayer = nextServePlayer,
            onPlayerChange = { player -> onFirstPlayerInPairToServeChoose(player.id) },
            enabled = enablePlayerCombobox
        )
    }

}
